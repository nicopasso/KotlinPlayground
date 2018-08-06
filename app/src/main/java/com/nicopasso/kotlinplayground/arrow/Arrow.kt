package com.nicopasso.kotlinplayground.arr

import arrow.core.*
import arrow.instances.extensions
import arrow.typeclasses.binding


class ArrowPlayground {

    val maybeInt: Option<Int> = Option(1) // Some(1)
    val result: Int? = maybeInt.orNull() // 1
    val absentInt: Option<Int> = None // None

    val a: Int? = null
    val maybeNull: Option<Int> = Option.fromNullable(a) // None

    val extensionInt: Option<Int> = 1.some() // Some(1)
    val extensionNoneInt: Option<Int> = none() // None

    // Option :: when
    val whenInt = when(maybeInt) {
        is None -> 0
        is Some -> maybeInt.t
    } // 1

    // Option :: fold
    val fold = maybeInt.fold({ 0 }, { it + 2 }) // 3

    // Option :: getOrElse
    val resultOnlyIfNone = absentInt.getOrElse { 0 } // 0

    // Option :: map
    val newValue = maybeInt.map { it + 1 } // Some(2)
    val mapWithNone = absentInt.map { it + 1 } // None

    // Option :: flatMap
    val maybeOne: Option<Int> = Option(1)
    val maybeTwo: Option<Int> = Option(2)

    val flatMapResult: Option<Int> = maybeOne.flatMap { one ->
        maybeTwo.map { two ->
            one + two
        }
    }

    // Arrow allows imperative style comprehension to make computing over Option values easy
    val maybeThree: Option<Int> = Option(3)

    // Option :: Monad (computing over dependent values)
    val someBindingResult = ForOption extensions {
        binding {
            val a = Some(1).bind()
            val b = Some(1 + a).bind()
            val c = Some(1 + b).bind()
            a + b + c
        }
    } //Some(value = 6)


    val bindingResult = ForOption extensions {
        binding {
            val x = none<Int>().bind()
            val y = Some(1 + x).bind()
            val z = Some(1 + y).bind()
            x + y + z
        }
    } // None

    // Option :: Functor (transofrming the inner content)
    val functor = Option.functor().run {
        Some(1).map { it + 1 }
    } // Some(2)

    // Option :: Applicative (computing over indepndent value)
    val applicative = ForOption extensions {
        tupled(Some(1), Some("Hello"), Some(20.0))
    } // Some(Tuple3(a=1, b="Hello", c=20.0)


}