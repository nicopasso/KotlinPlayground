package com.nicopasso.kotlinplayground

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

sealed class Block(val s: String)
class CodeBlock(st: String) : Block(st)
class Text(st: String) : Block(st)
class Header(st: String) : Block(st)

sealed class PlaygroundElement(val obj: Any) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        println("class: " + javaClass + ", other class: " + other?.javaClass)
        return other?.javaClass != javaClass
    }
}
class Code(st: String): PlaygroundElement(st)
class Documentation(val blocks: List<Block>): PlaygroundElement(blocks)

fun playground(blocks: ArrayList<Block>) : MutableList<PlaygroundElement> {

    var result = mutableListOf<PlaygroundElement>()

    //Starting point: NOT WORKING!
    /*for (block in blocks) {
        when(block) {
            is CodeBlock -> result.add(Code(block.s))
            else -> result.add(Documentation(arrayListOf(block)))
        }
    }*/

    //First try
    /*val nonCodeBlocks = arrayListOf<Block>()
    fun flush() {
        if (!nonCodeBlocks.isEmpty()) {
            result.add(Documentation(nonCodeBlocks))
            nonCodeBlocks.clear()
        }
    }

    for (block in blocks) {
        when(block) {
            is CodeBlock -> {
                flush()
                result.add(Code(block.s))
            }
            else -> nonCodeBlocks.add(block)
        }
    }

    flush()*/

    //Second try
    for (block in blocks) {
        when(block) {
            is CodeBlock -> {
                result.add(Code(block.s))
            }
            else -> {
                val last = result.last()
                var newBlocks = listOf(block)
                if (last is Documentation) {
                    result.removeAt(result.size - 1)
                    newBlocks = last.blocks + newBlocks
                }
                result.add(Documentation(newBlocks))
            }

        }
    }

    return result
}

fun <A> AssertEqual(left: ArrayList<A>, right: ArrayList<A>) {
    val message = "\n" + left.toString() + "\nis not equal to:\n" + right.toString() + "\n"

    assertEquals(message, left, right)
}

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test_playground() {
        val sample = arrayListOf<Block>(
                CodeBlock("Kotlin sample code"),
                Header("My Header"),
                Text("Hello"),
                CodeBlock("more"))

        val result = arrayListOf(
                Code("Kotlin sample code"),
                Documentation(arrayListOf(Header("My Header"), Text("Hello"))),
                Code("more"))

        val transform = playground(sample)

        assertEquals(transform, result)
    }


}
