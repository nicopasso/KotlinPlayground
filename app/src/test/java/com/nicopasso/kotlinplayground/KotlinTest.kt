package com.nicopasso.kotlinplayground

import io.kotlintest.specs.ShouldSpec
import io.kotlintest.specs.StringSpec


class MyScreen: ShouldSpec() {

    init {

        should("My first kotlintest test") {
            // ....
        }
    }

}

/**
 * fun should(name: String, test: () -> Unit): TestCase {
 *   val testCase = TestCase()
 *   current.addTestCase(testCase)
 *   return testCase
 * }
 */


class MyOtherScreen: StringSpec() {

    init {

        "My second kotlintest test" {
            // ...
        }

    }
}

/**
 * operator fun String.invoke(test: () -> Unit): TestCase {
 *   val testCase = TestCase()
 *   current.addTestCase(testCase)
 *   return testCase
 * }
 */

