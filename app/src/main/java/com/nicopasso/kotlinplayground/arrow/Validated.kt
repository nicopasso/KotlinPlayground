package com.nicopasso.kotlinplayground.arrow

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.data.NonEmptyList
import arrow.data.Validated
import arrow.data.invalid
import arrow.data.valid


/**
 *  USE CASE:
 *
 *  web form to sign up for an account: username, password and submit button. Response comes back
 *  saying username can't have dashes in it, so we make some changes and resubmit. Passwords need to have
 *  at least one capital letter. Change, resubmit. Password needs to have at least one number...
 *  It'd be nice to have all of these errors reported simultaneously.
 *  Enter Validated.
 *
 */
abstract class Read<A> {

    abstract fun read(s: String): Option<A>

    companion object {

        val stringRead: Read<String> = object: Read<String>() {
            override fun read(s: String): Option<String> = Option(s)
        }

        val intRead: Read<Int> = object: Read<Int>() {
            override fun read(s: String): Option<Int> =
                    if (s.matches(Regex("-?[0-9]+"))) Option(s.toInt()) else None
        }

    }
}

sealed class ConfigError {
    data class MissingConfig(val field: String): ConfigError()
    data class ParseConfig(val field: String): ConfigError()
}

data class Conifg(val map: Map<String, String>) {

    fun <A> parse(read: Read<A>, key: String): Validated<ConfigError, A> {
        val v = Option.fromNullable(map[key])
        return when(v) {
            is Some -> {
                val s = read.read(v.t)
                when(s) {
                    is Some -> s.t.valid()
                    is None -> ConfigError.ParseConfig(key).invalid()
                }
            }
            is None -> Validated.Invalid(ConfigError.MissingConfig(key))
        }
    }
}

class Validator {

    fun <E, A, B, C> parallelValidate
                (v1: Validated<E, A>, v2: Validated<E, B>, f: (A, B) -> C): Validated<NonEmptyList<E>, C> {
        return when {
            v1 is Validated.Valid && v2 is Validated.Valid -> Validated.Valid(f(v1.a, v2.a))
            v1 is Validated.Valid && v2 is Validated.Invalid -> v2.toValidatedNel()
            v1 is Validated.Invalid && v2 is Validated.Valid -> v1.toValidatedNel()
            v1 is Validated.Invalid && v2 is Validated.Invalid -> Validated.Invalid(NonEmptyList(v1.e, listOf(v2.e)))
            else -> throw IllegalStateException("Not possible value")
        }
    }

}