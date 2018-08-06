package com.nicopasso.kotlinplayground.arrow

import arrow.core.*
import arrow.instances.ForEither
import arrow.typeclasses.binding


class ArrowEitherPlayground {

    /**
     * Example: a series of functions that will
     * 1) parse a string into an integer
     * 2) calculate the reciprocal
     * 3) convert the reciprocal into a string
     */

    // Using exception-throwing code style, we could write something like
    fun parseOld(s: String): Int =
            if (s.matches(Regex("-?[0-9]+"))) s.toInt()
            else throw NumberFormatException("$s is not a valid integer")

    fun reciprocalOld(i: Int): Double =
            if (i == 0) throw IllegalArgumentException("Cannot take reciprocal of 0.")
            else 1.0 / i

    fun stringify(d: Double): String = d.toString()





    // Either style
    fun parse(s: String): Either<NumberFormatException, Int> =
            if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
            else Either.Left(NumberFormatException("$s is not a valid integer"))

    fun reciprocal(i: Int): Either<IllegalArgumentException, Double> =
        if (i == 0) Either.Left(IllegalArgumentException("Cannot take reciprocal of 0."))
        else Either.Right(1.0 / i)

    fun magic(s: String): Either<Exception, String> =
            parse(s).flatMap { reciprocal(it) }.map { stringify(it) }

    val resultLeft = magic("0") // Left(a=java.lang.IllegalArgumentException: Cannot take reciprocal of 0.)

    val resultRight = magic("1") // Right(b=1.0)

    val resultNaN = magic("Not a number") // //Left(a=java.lang.NumberFormatException: Not a number is not a valid integer.)

    val x = magic("2")
    val value = when(x) {
        is Either.Left -> when(x.a) {
            is NumberFormatException -> "Not a number!"
            is IllegalArgumentException -> "Can't take reciprocal of 0!"
            else -> "Unknown error"
        }
        is Either.Right -> "Got reciprocal: ${x.b}"
    } // Got reciprocal: 0.5





    // EIther with ADT Style
    sealed class Error {
        object NotANumber: Error()
        object NoZeroReciprocal: Error()
    }

    fun parseADT(s: String): Either<Error, Int> =
        if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
        else Either.Left(Error.NotANumber)

    fun reciprocalADT(i: Int): Either<Error, Double> =
        if (i == 0) Either.Left(Error.NoZeroReciprocal)
        else Either.Right(1.0 / i)

    fun magicADT(s: String): Either<Error, String> =
        parseADT(s).flatMap { reciprocalADT(it) }.map { stringify(it) }

    val y = magicADT("2")
    val valueADT = when(y) {
        is Either.Left -> when(y.a) {
            is Error.NotANumber -> "Not a number!"
            is Error.NoZeroReciprocal -> "Can't take reciprocal of 0!"
        }
        is Either.Right -> "Got reciprocal: ${y.b}"
    } // Got reciprocal: 0.5


    /**
     * SYNTAX and CONVENIENT METHODS
     */

    // map applies only on Right instances. To work with Left instances we need mapLeft
    val l: Either<Int, Int> = Either.Left(7)
    val mapLeftResult = l.mapLeft { it + 1 } // Left(a=8)


    // Swap!
    val r: Either<String, Int> = Either.Right(7)
    val swapped =  r.swap() // Left(a=7)

    // Arbitrary data types
    val seven = 7.right() // Right(b=7)
    val hello = "hello".left() // Left(a=hello)
    val contains = seven.contains(7) // true
    val getOrElse = hello.getOrElse { 7 } // 7
    val getOrHandle = hello.getOrHandle { "$it world!" } // hello world!

    // For creatubg Either instances based on a predicate
    val cond = Either.cond(true, { 42 }, { "Error" }) // Right(b=42)
    val condFalse = Either.cond(false, { 42 }, { "Error" }) // Left(a=Error)


    // Fold: will extracts the value from Either, or provide a default if the value is Left
    val f: Either<Int, Int> = 5.right()
    val fold = f.fold( { 1 }, { it + 3 } ) // 8
    val fl: Either<Int, Int> = 7.left()
    val fold2 = f.fold( { 1 }, { it + 3 } ) // 1


    // Real example
    val real : Either<Throwable, Int> = Either.Left(NumberFormatException())
    val httpStatusCode = real.getOrHandle {
        when(it) {
            is NumberFormatException -> 400
            else -> 500
        }
    }



    // FUNCTOR
    val functor = ForEither<Int>() extensions {
        Right(1).map { it + 1 }
    } // Right(b=2)

    // APPLICATIVE
    val applicative = ForEither<Int>() extensions {
        tupled(Either.Right(1), Either.Right("a"), Either.Right(2.0))
    } // Right(b=Tuple3(a=1, b=a, c=2.0)


    // MONAD
    val monad = ForEither<Int>() extensions {
        binding {
            val a = Either.Right(1).bind()
            val b = Either.Right(1 + a).bind()
            val c = Either.Right(1 + b).bind()
            a + b + c
        }
    } // Right(6)


}