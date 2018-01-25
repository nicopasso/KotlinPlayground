package com.nicopasso.kotlinplayground.sealed_classes

/*
   ___     ___     ___     _       ___     ___              ___     _       ___     ___     ___     ___     ___
  / __|   | __|   /   \   | |     | __|   |   \     o O O  / __|   | |     /   \   / __|   / __|   | __|   / __|
  \__ \   | _|    | - |   | |__   | _|    | |) |   o      | (__    | |__   | - |   \__ \   \__ \   | _|    \__ \
  |___/   |___|   |_|_|   |____|  |___|   |___/   TS__[O]  \___|   |____|  |_|_|   |___/   |___/   |___|   |___/
_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""| {======|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|
"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'./o--000'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'

 */

/**
 * A sealed class can be subclassed and may include abstract methods which means sealed classes
 * are abstract implicitly.
 * IMPORTANT: All the subclasses must be declared in the same file as the sealed class itself.
 *
 * This feature allows to define class HIERARCHIES that are RESTRICTED IN ITS' TYPES. Since all
 * the subclasses need to be defined inside the file of the sealed class, there's no chance of unknown
 * subclasses which the compiler doesn't know about.
 *
 * As opposed of enums, subclasses of sealed classes can be INSTANTIATED MULTIPLE TYPES
 * and can contain STATE.
 *
 * The main advantage reveals itself if it's used in when expressions.
 */
open class NoSealedMammal(val name: String)
class NSCat(val catName: String) : NoSealedMammal(catName)
class NSHuman(val humanName: String, val job: String) : NoSealedMammal(humanName)

//Usage:
//the else branch is mandatory because the compiler cannot just verify all possible cases
fun greetNSMammal(mammal: NoSealedMammal): String = when(mammal) {
    is NSHuman -> "Hello ${mammal.name}; You're working as a ${mammal.job}"
    is NSCat -> "Hello ${mammal.name}"
    else -> "Hello unknown"
}


//SEALED TO THE RESCUE!
sealed class Mammal(val name: String)
class Cat(val catName: String): Mammal(catName)
class Human(val humanName: String, val job: String): Mammal(humanName)

fun greetMammal(mammal: Mammal): String = when(mammal) {
    is Human -> "Hello ${mammal.name}; You're working as a ${mammal.job}"
    is Cat -> "Hello ${mammal.name}"
    // 'else' clause not required, all the cases covered
}
