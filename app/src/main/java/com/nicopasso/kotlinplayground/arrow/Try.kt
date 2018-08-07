package com.nicopasso.kotlinplayground.arrow

import arrow.core.*
import arrow.instances.extensions
import arrow.typeclasses.binding
import arrow.typeclasses.bindingCatch


class ArrowTryPlayground {

    /**
     * Try represents a computation that can result in an A result (as long as the computation is successful)
     * or in an excpetion if something has gone wrong.
     * Two implementations: Success<A> or Failure.
     *
     * Same thing as Either<Throwable, A>? Kinda. Wither and Try have different use cases.
     *
     * We use Try as a substitute for the well-known try-catch
     */

    // Models
    open class GeneralException: Exception()
    class NoConnectionException: GeneralException()
    class AuthorizationException: GeneralException()

    fun checkPermission() {
        throw AuthorizationException()
    }

    fun getLotteryNumbersFromCloud(): List<String> {
        throw NoConnectionException()
    }

    fun getLotteryNumbers(): List<String> {
        checkPermission()

        return getLotteryNumbersFromCloud()
    }

    // The traditional way to control this would be to use a try-catch block
    val tryCatch = try {
        getLotteryNumbers()
    } catch (e: NoConnectionException) {
        //...
    } catch (e: AuthorizationException) {
        //...
    }

    // However, we could use Try to retrieve the computation result in a much cleaner way
    val lotteryTry = Try { getLotteryNumbers() } // Failure(exception=Line_1$AuthorizationException)

    val default = lotteryTry.getOrDefault { emptyList() } // []

    //if the underlying failure is useful to determine the default value we can use getOrElse
    val getOrElse = lotteryTry.getOrElse { ex: Throwable -> ex.localizedMessage }

    // Perform a check on possible success
    val possibleSuccess = lotteryTry.filter { it.size < 4 } // Failure(exception=Line_1$AuthorizationException)

    // Recover from a particular error
    val recover = lotteryTry.recover { emptyList() } // Success(value=[])

    // Different computation that can fail, use recoverWith to recover from an error by returning a Try
    enum class Source {
        CACHE, NETWORK
    }

    fun getLotteryNum(source: Source): List<String> {
        checkPermission()

        return getLotteryNumbersFromCloud()
    }

    val recoverWith = Try { getLotteryNum(Source.NETWORK) }.recoverWith {
        Try { getLotteryNum(Source.CACHE) }
    } // Failure(exception=Line_1$AuthorizationException)

    // Fold
    val fold = lotteryTry.fold(
        { emptyList<String>() }, // Failure into a new value
        { it.filter { it.toIntOrNull() != null }}) // Success into a new value
    // []

    // Transform
    val transform = lotteryTry.transform(
        { Try { it.map { it.toInt() } } },
        { Try.just(emptyList()) }) // Success(value=[])

    // Functor
    val functor = ForTry extensions {
        Try { "3".toInt() }.map { it + 1 }
    } // Success(value=4)

    // Applicative
    val applicative = ForTry extensions {
        tupled(Try { "3".toInt() }, Try { "5".toInt() }, Try { "nope".toInt() })
    } // Failure(exception=java.lang.NumberFormatException: For input string: "nope")


    // Monad
    val monad = ForTry extensions {
        binding {
            val a = Try { "3".toInt() }.bind()
            val b = Try { "4".toInt() }.bind()
            val c = Try { "5".toInt() }.bind()
            a + b + c
        }
    } // Success(value=12)

    val failureMonad = ForTry extensions {
        binding {
            val a = Try { "none".toInt() }.bind()
            val b = Try { "4".toInt() }.bind()
            val c = Try { "5".toInt() }.bind()
            a + b + c
        }
    } //Failure(exception=java.lang.NumberFormatException: For input string: "none")

    // Computing over dependent values automatically lifted to the context of Try
    val liftedMonad = ForTry extensions {
        bindingCatch {
            val a = "none".toInt()
            val b = "4".toInt()
            val c = "5".toInt()
            a + b + c
        }
    } //Failure(exception=java.lang.NumberFormatException: For input string: "none")
}