package com.nicopasso.kotlinplayground.type_variants

//COVARIANT produce = output = out
interface Production<out T> {
    fun produce() : T
}

//CONTRAVARIANT consume = input = in
interface Consumer<in T> {
    fun consume(item: T)
}

//INVARIANT
interface ProductionConsumer<T> {
    fun produce() : T
    fun consume(item: T)
}

open class Food
open class FastFood: Food()
class Burger: FastFood()

//Step 1 - Burger Production
class FoodStore: Production<Food> {
    override fun produce(): Food {
        println("Produce food")
        return Food()
    }
}

class FastFoodStore: Production<FastFood> {
    override fun produce(): FastFood {
        println("Produce fast food")
        return FastFood()
    }
}

class InOutBurger: Production<Burger> {
    override fun produce(): Burger {
        println("Produce burger")
        return Burger()
    }
}

//For 'out' generic, we could assign a class of subtype to class of super-type
val production1: Production<Food> = FoodStore()
val production2: Production<Food> = FastFoodStore()
val production3: Production<Food> = InOutBurger()

//Step 2 - Burger Consumer
class Everybody: Consumer<Food> {
    override fun consume(item: Food) {
        println("Eat food")
    }
}

class ModernPeople: Consumer<FastFood> {
    override fun consume(item: FastFood) {
        println("Eat fast food")
    }
}

class American: Consumer<Burger> {
    override fun consume(item: Burger) {
        println("Eat burger")
    }
}

//For 'in' generic, we could assign a class of super-type to a class of subtype
val consumer1: Consumer<Burger> = Everybody()
val consumer2: Consumer<Burger> = ModernPeople()
val consumer3: Consumer<Burger> = American()


