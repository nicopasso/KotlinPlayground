package com.nicopasso.kotlinplayground.conventions

/*
  / __|    ___    _ _     __ __    ___    _ _     | |_     (_)     ___    _ _      ___
 | (__    / _ \  | ' \    \ V /   / -_)  | ' \    |  _|    | |    / _ \  | ' \    (_-<
  \___|   \___/  |_||_|   _\_/_   \___|  |_||_|   _\__|   _|_|_   \___/  |_||_|   /__/_
_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|
"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'
 */

//If we define a special method 'plus' in our class, we can use the + operator BY CONVENTION
data class Fraction(val numerator: Int, val denominator: Int): Comparable<Fraction> {
    val decimal by lazy {
        numerator.toDouble() / denominator
    }

    override fun toString(): String = "$numerator/$denominator"

    //Overloading operators makes it possible to use + in other classes than Int or String.
    operator fun plus(add: Fraction) =
            if (this.denominator == add.denominator) {
                Fraction(this.numerator + add.numerator, denominator)
            } else {
                val a = this * add.denominator //translated to this.times(add.denominator) at compile-time
                val b = add * this.denominator
                Fraction(a.numerator + b.numerator, a.denominator)
            }

    operator fun times(num: Int) = Fraction(numerator * num, denominator * num)

    //I need to add "override" keyword because I'm implementing the Comparable interface
    override operator fun compareTo(other: Fraction) = decimal.compareTo(other.decimal)

    //***********
    //  INVOKE
    //***********
    //Kotlin is designed to enable creation of great DSL. Besides function literals with receiver, another important feature is
    //the INVOKE convention that makes objects CALLABLE AS A FUNCTION!!!
    operator fun invoke(prefix: String = "") = println(prefix + toString())

}

//Index operator for the Fraction class. Here we can see that we can extend the class by overriding an operator
operator fun Fraction.get(ind: Int) = when(ind) {
    0 -> numerator
    1 -> denominator
    else -> IllegalArgumentException("Index must be 0 or 1")
}

//++ operator
operator fun Fraction.inc() = Fraction(this.numerator + 1, this.denominator)

/****************************
  DESTRUCTING DECLARATIONS
*****************************/
//We want to use the destructing declaration for the Fraction class in the same way we use it for map
//Something like for ((i, s) in map)
//when we make use of DD (v1, v2, ..., vn) those variables are initialized by calls to functions with the name component1, component2, ..., componentN.
//Let's see how Map works
public inline operator fun <K, V> Map.Entry<K, V>.component1(): K = key
public inline operator fun <K, V> Map.Entry<K, V>.component2(): V = value
//These functions are created automatically for every "data" class by default, therefore the Fraction class can be used in destructing declarations, too
//The "componentX" functions are generated for every property declared in the primary constructor of a data class