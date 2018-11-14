package com.nicopasso.kotlinplayground.arrow

import arrow.Kind
import arrow.core.Either
import arrow.core.left
import arrow.data.EitherT
import arrow.data.ListK
import arrow.data.value
import arrow.effects.DeferredK
import arrow.effects.ForDeferredK
import arrow.effects.deferredk.monad.monad
import arrow.effects.fix
import arrow.effects.k
import arrow.effects.typeclasses.Async
import arrow.instances.monad
import arrow.typeclasses.binding
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.*

data class GithubUser(val login: String)
data class GistFile(val fileName: String?)
data class Gist(
        val files: Map<String, GistFile>,
        val description: String?,
        val comments: Long,
        val owner: GithubUser)

interface GistApiDataSource<F> {
    fun publicGistsForUser(username: String): Kind<F, ListK<Gist>>
}

class Async {

    val moshi = Moshi.Builder().build()
    val type = Types.newParameterizedType(ListK::class.java, Gist::class.java)
    val moshiAdapter = moshi.adapter<ListK<Gist>>(type)

    //1) No FP
    fun publicGistsForUser(username: String): ListK<Gist> {
        val (_, _, result) = "https://api.github.com/users/$username/gists".httpGet().responseString() //Blocking IO
        return when (result) {
            is Result.Failure -> throw result.getException() //blows the stack
            is Result.Success -> moshiAdapter.fromJson(result.value)!!
        }
    }

    //2) Either
    fun publicGistsForUserEither(username: String): Either<Throwable, ListK<Gist>> {
        val (_, _, result) = "https://api.github.com/users/$username/gists".httpGet().responseString() //Blocking IO
        return when (result) {
            is Result.Failure -> result.getException().left() //blows the stack
            is Result.Success -> Either.Right(moshiAdapter.fromJson(result.value)!!)
        }
    }

    //Either or Option directly: they cannot deferred the effect evaluation!!!!
    //They doesn't solve the fact the we're still running a blocking operation

    val funWithEither = publicGistsForUserEither("-__unknown_user__-")
    //Left(a=java.net.UnknownHostException: api.github.com)

    //3) Many choose to go non-blocking with Coroutines
    fun deferredPublicGistsForUserEither(username: String): Deferred<Either<Throwable, ListK<Gist>>> =
        runBlocking<Deferred<Either<Throwable, ListK<Gist>>>> {
            async {
                val (_, _, result) = "https://api.github.com/users/$username/gists".httpGet().responseString() //Blocking IO
                when (result) {
                    is Result.Failure -> result.getException().left() //blows the stack
                    is Result.Success -> Either.Right(moshiAdapter.fromJson(result.value)!!)
                }
            }
        }

    val funWithCoroutines = deferredPublicGistsForUserEither("-__unknown_user__-")
    //DeferredCoroutine(Active)@2cc5f775

    suspend fun allGists(): List<Gist> {
        val result1: Either<Throwable, ListK<Gist>> = deferredPublicGistsForUserEither("-__unknown_user__-").await()
        val result2: Either<Throwable, ListK<Gist>> = deferredPublicGistsForUserEither("-__unknown_user2__-").await()
        return when {
            result1 is Either.Right && result2 is Either.Right -> result1.b + result2.b
            else -> emptyList()
        }
    }

    //4) Monad Transformers
    fun allGistsWithMonads(): DeferredK<Either<Throwable, List<Gist>>> =
            EitherT
                    .monad<ForDeferredK, Throwable>(DeferredK.monad()) //ForDefferedK is the Higher-Kind representation of Deferred
                    .binding {

                        val result1 = EitherT(deferredPublicGistsForUserEither("-__unknown_user__-").k()).bind()
                        val result2 = EitherT(deferredPublicGistsForUserEither("-__unknown_user1__-").k()).bind()
                        result1 + result2
                    }.value().fix()

    //Arrow's delegation to 'async' is always lazy
    val allGists = allGistsWithMonads()
    //DeferredK(deferred=LazyDeferredCoroutine{New}@789w8761

    //5) Async type class
    class DefaultGistApiDataSource<F>(private val async: Async<F>): GistApiDataSource<F>, Async<F> by async {

        val moshi = Moshi.Builder().build()
        val type = Types.newParameterizedType(ListK::class.java, Gist::class.java)
        val moshiAdapter = moshi.adapter<ListK<Gist>>(type)

        override fun publicGistsForUser(username: String): Kind<F, ListK<Gist>> {
            async { proc: (Either<Throwable, ListK<Gist>>) -> Unit ->
                "https://api.github.com/users/$username/gists".httpGet().responseString { _, _, result ->
                    when (result) {
                        is Result.Failure -> proc(result.getException().left())
                        is Result.Success -> proc(Either.right(moshiAdapter.fromJson(result.value)!!))
                    }
                }

            }
        }

    }

}