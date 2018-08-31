package com.nicopasso.kotlinplayground.functional_kotlin

sealed class FunctionalList<out T> {
    object Nil: FunctionalList<Nothing>()
    data class Construct<out T>(val head: T, val tail: FunctionalList<T>): FunctionalList<T>()


    private fun initializeFunList(vararg numbers: Int): FunctionalList<Int> {
        return if (numbers.isEmpty()) {
            Nil
        } else {
            Construct(numbers.first(), initializeFunList(*numbers.drop(1).toTypedArray().toIntArray()))
        }
    }

    //Without the initializeFunList() method:
    //val numbers = Construct(1, Construct(2, Construct(3, Construct(4, Nil))))
    val numbers = initializeFunList(1, 2, 3, 4)

    private fun forEach(f: (T) -> Unit) {
        tailrec fun go(list: FunctionalList<T>, f: (T) -> Unit) {

            when (list) {
                is Construct -> {
                    f(list.head)
                    go(list.tail, f)
                }
                is Nil -> Unit //Do nothing...
            }

        }

        go(this, f)
    }

    val applyForEach = numbers.forEach { i -> println("i = $i")}


    private fun <R> fold(init: R, f: (R, T) -> R): R {
        tailrec fun go(list: FunctionalList<T>, init: R, f: (R, T) -> R): R = when(list) {
            is Construct -> go(list.tail, f(init, list.head), f)
            is Nil -> init
        }

        return go(this, init, f)
    }

    val sum = numbers.fold(0) { acc, i -> acc + i }

    private fun reverse(): FunctionalList<T> = fold(Nil as FunctionalList<T>) { acc, i -> Construct(i, acc) }

    private fun <R> foldRight(init: R, f: (R, T) -> R): R {
        return this.reverse().fold(init, f)
    }

    private fun <R> map(f: (T) -> R): FunctionalList<R> {
        return foldRight(Nil as FunctionalList<R>) { tail, head -> Construct(f(head), tail) }
    }

}

