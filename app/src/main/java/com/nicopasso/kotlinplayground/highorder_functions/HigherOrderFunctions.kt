package com.nicopasso.kotlinplayground.highorder_functions

/*
  _  _      _      __ _   _                                                  _                                __                            _        _
 | || |    (_)    / _` | | |_      ___      _ _    ___     ___      _ _   __| |    ___      _ _     o O O    / _|  _  _    _ _      __     | |_     (_)     ___    _ _      ___
 | __ |    | |    \__, | | ' \    / -_)    | '_|  |___|   / _ \    | '_| / _` |   / -_)    | '_|   o        |  _| | +| |  | ' \    / _|    |  _|    | |    / _ \  | ' \    (_-<
 |_||_|   _|_|_   |___/  |_||_|   \___|   _|_|_   _____   \___/   _|_|_  \__,_|   \___|   _|_|_   TS__[O]  _|_|_   \_,_|  |_||_|   \__|_   _\__|   _|_|_   \___/  |_||_|   /__/_
_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|     |_|"""""|_|"""""|_|"""""|_|"""""|_|"""""| {======|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|
"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'./o--000'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'

 */

//A high-order function is a function that takes another function as an argument or returns one.
// In Kotlin, functions can be represented as values using lambdas or function references.
// Therefore, a higher-order function is any function to which you can pass a lambda or a
// function reference as an argument, or a funcion which returns one, or both.

//**************************
//      Function types
//**************************
val sumInferred = { x: Int, y: Int -> x + y }
val actionInferred = { println(42) }

//To decalre a function type, you put the function parameter types in parentheses, followed by
//an arrow and the return type of the function
val sum: (Int, Int) -> Int = { x, y -> x + y}
val action: () -> Unit = { println(42) } //for a function type declaration Unit cannot be omitted

//As with any other function, the return type of a function type can be marked as nullable
var canReturnNull: (Int, Int) -> Int? = { _, _ -> null }

//It's also possible to define a nullable variable of a function type.
var funOrNull: ((Int, Int) -> Int)? = null

//It's possible to specify names for parameters of a function type
fun performRequest(url: String, callback: (code: Int, content: String) -> Unit) {}
val url = "http://www.plugintheworld.com"
// performRquest(url) { code, content -> /*...*/ }
// performRquest(url) { id, page -> /*...*/ }


//***********************************
//Implementing higher-order functions
//*******************+***************
fun twoAndThree(operation: (Int, Int) -> Int) {
    val result = operation(2, 3)
    println("The result is $result")
}

//twoAndThree { a, b -> a + b }
//The result is 5
//twoAndThree { a, b -> a * b }
//The result is 6

//Filter higher-order function
fun String.filter(predicate: (Char) -> Boolean) : String {
    val sb = StringBuilder()
    for (index in 0 until length) {
        val element = get(index)
        if (predicate(element)) sb.append(element)
    }
    return sb.toString()
}


//***********************************
//Default and null values for params
//*******************+***************
//when we declare a parameter of a function ype, you can also specify its default value.
//This kind of implementation is flexible because we don't need to define all the params
// when we're calling the function
fun <T> Collection<T>.joinToString(
        separator: String = ", ",
        prefix: String = "",
        postfix: String = ""): String {
    val result = StringBuilder(prefix)

    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }

    result.append(postfix)
    return result.toString()
}

//But the code uses append, which always converts the object to a String using the toString() method.
//We can solve thi by defining a param of a function type and specify a default value for it as a lambda
fun <T> Collection<T>.betterJoinToString(
        separator: String = ", ",
        prefix: String = "",
        postfix: String = "",
        transform: (T) -> String = { it.toString() }) : String {
    val result = StringBuilder(prefix)

    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(transform(element))
    }

    result.append(postfix)
    return result.toString()
}

val letters = listOf("Alpha", "Beta")
// >>> println(letters.betterJoinToString { it.toLowerCase() })
//     alpha, beta

// >>> println(letters.betterJoinToString(separator= "! ", postfix = "! ", transform =  { it.toUpperCase() }))
//     ALPHA! BETA!

//We could pass a null function as the transform parameter and the compiler will throw an error.
//What we could do to solve this is to use the 'invoke' method on a function
fun <T> Collection<T>.saferJoinToString(
        separator: String = ", ",
        prefix: String = "",
        postfix: String = "",
        transform: ((T) -> String)? = null) : String {

    val result = StringBuilder(prefix)

    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(transform?.invoke(element) ?: element.toString())
    }

    result.append(postfix)
    return result.toString()
}


//***********************************
// Returning function from functions
//*******************+***************
enum class Delivery { STANDARD, EXPEDITED }
class Order(val itemCount: Int)

fun getShippingCostCalculator(delivery: Delivery): (Order) -> Double {
    if (delivery == Delivery.EXPEDITED) {
        return { order -> 6 + 2.1 * order.itemCount }
    }

    return { order -> 1.2 * order.itemCount }
}

val calculator: (Order) -> Double = getShippingCostCalculator(Delivery.EXPEDITED)
// >>> println("Shipping costs ${calculator(Order(3)}")
//     Shipping costs 12.3


//Another example
data class Person(val firstName: String, val lastName: String, val phoneNumber: String?)

class ContactListFilters {
    var prefix: String = ""
    var onlyWithPhoneNumber: Boolean = false

    fun getPredicate(): (Person) -> Boolean {
        val startWithPrefix = { p: Person ->
            p.firstName.startsWith(prefix) || p.lastName.startsWith(prefix)
        }

        if (!onlyWithPhoneNumber) {
            return startWithPrefix
        }

        return { startWithPrefix(it) && it.phoneNumber != null }
    }
}

val contacts = listOf(Person("Dmitry", "Jemerov", "123-4567"),
        Person("Niccolò", "Passolunghi", null))
val contactListFilters = ContactListFilters()
/*with(contactListFilters) {
    prefix = "Dm"
    onlyWithPhoneNumber = true
}
println(contacts.filter(contactListFilters.getPredicate())) */
//[Person(firstName=Dmitrym lastName=Jemerov, phoneNumber=123-4567)]


//***************************************
// Removing duplications through lambdas
//*******************+*******************
//Many kinds of code duplication can now be eliminated by using lambda expressions
data class SiteVisit(val path: String, val duration: Double, val os: OS)
enum class OS { WINDOWS, LINUX, MAC, IOS, ANDROID }

val log = listOf(
        SiteVisit("/", 34.0, OS.WINDOWS),
        SiteVisit("/", 22.0, OS.MAC),
        SiteVisit("/login", 12.0, OS.WINDOWS),
        SiteVisit("/signup", 8.0, OS.IOS),
        SiteVisit("/", 16.3, OS.ANDROID)
)

val averageWindowsDuration = log.filter { it.os == OS.WINDOWS }.map(SiteVisit::duration).average()
//23.0

//Now suppose we need to calculate the same statistic for Mac, iOS and Android. We want to avoid code repetition
//and to do that we can extract the platform as a parameter
fun List<SiteVisit>.averageDurationFor(os: OS) = filter { it.os == os }.map(SiteVisit::duration).average()
//println(log.averageDurationFor(OS.WINDOWS))
//println(log.averageDurationFor(OS.MAC))