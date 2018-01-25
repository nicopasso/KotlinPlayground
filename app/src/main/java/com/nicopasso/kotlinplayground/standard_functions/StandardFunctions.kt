package com.nicopasso.kotlinplayground.standard_functions

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import java.io.File

/*
   ___     _                         _                       _               ___                            _        _
  / __|   | |_    __ _    _ _     __| |   __ _      _ _   __| |     o O O   | __|  _  _    _ _      __     | |_     (_)     ___    _ _      ___
  \__ \   |  _|  / _` |  | ' \   / _` |  / _` |    | '_| / _` |    o        | _|  | +| |  | ' \    / _|    |  _|    | |    / _ \  | ' \    (_-<
  |___/   _\__|  \__,_|  |_||_|  \__,_|  \__,_|   _|_|_  \__,_|   TS__[O]  _|_|_   \_,_|  |_||_|   \__|_   _\__|   _|_|_   \___/  |_||_|   /__/_
_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""| {======|_| """ |_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|_|"""""|
"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'./o--000'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'

 */

// SCOPING FUNCTIONS (run, with, T.run, T.let, T.also, T.apply)
fun test() {
    var mood = "I am sad"
    run {
        //Inside here we have a different scope and we can redefined a variable with the same name
        val mood = "I am happy"
        println(mood) //I am happy

        //the run block returns something i.e. the last object within the scope
        val returnedString = "returned!"
        returnedString
    }.length //is the length of "returnedString"

    println(mood) //I am sad
}

class StdFunctionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        val webView = WebView(this)

        //**************************
        //      with VS T.run
        //**************************
        with(webView.settings) {
            this?.javaScriptEnabled = true
            this?.databaseEnabled = true
        }

        webView.settings?.run { // <---- better this one in this case
            javaScriptEnabled = true
            databaseEnabled = true
        }

        //**************************
        //   this VS it ARGUMENT
        //**************************
        val stringVariable: String? = "variable"
        //T.run is made as extension function calling block: T.() hence all within the scope, the T
        //could be referred as 'this'.
        stringVariable?.run {
            println("The length of this String is $length")
        }

        //T.let is sending itself into the function i.e. block: (T), hence this is like a lambda argument sent to it.
        stringVariable?.let {
            println("The length of this String is ${it.length}")
        }

        //Pros and cons
        // - The T.let provides a clearer distingiush use of the given variable function/member
        //   vs the external class funtion/memeber
        // - in the event that 'this' can't be omitted (when it's sent as a parameter), 'it' is shorter than 'this'
        // - The T.let allow better naming of the converted used variable. It's possible to convert
        //   'it' to some other name
        stringVariable?.let { nonNullString ->
            println("The non null string is $nonNullString")
        }

        //**************************
        // Return this vs other type
        //**************************
        stringVariable?.let {
            println("The length of this String is ${it.length}")
        }

        stringVariable?.also {
            println("The length of this String is ${it.length}")
        }

        //The T.let returns a different type of value, while T.also returns the T itself

        val originalAlphabet = "abcdefghijklmnopqrstuvwxyz"
        //Change the value and send it to the next chain
        originalAlphabet.let {
            println("The original alphabet is $it")
            it.reversed()
        }.let {
            println("The reversed alphabet is $it")
            it.length
        }.let {
            println("The length is $it")
        }

        //I cannot do the same with an 'also' chain
        originalAlphabet.also {
            println("The original alphabet is $it")
            it.reversed()
        }.also {
            println("The reversed alphabet is $it") // "abcdefghijklmnopqrstuvwxyz"
            it.length
        }.also {
            println("The length is $it") // "abcdefghijklmnopqrstuvwxyz"
        }

        //The right 'also' chain use is the following
        originalAlphabet.also {
            println("The original alphabet is $it")
        }.also {
            println("The reversed alphabet is ${it.reversed()}")
        }.also {
            println("The length is ${it.length}")
        }

        //'also' has some good pros
        // - It can provides a very clear separation process on the same object
        // - It can be very powerful for self manipulation before being used, i.e. making a chaining builder operation

        //Combination of 'let' and 'also'
        fun makeDir(path: String) = path.let { File(it) }.also { it.mkdir() }

        //**************************
        //         T.apply
        //**************************

        //T.apply is an extension function, sends 'this' as it's argument AND returns 'this'
        fun createInstance(args: Bundle): Fragment {
            val fragment = Fragment()
            fragment.arguments = args
            return fragment
        }

        fun smartCreateInstance(args: Bundle) = Fragment().apply { arguments = args }

        fun createIntent(intentData: String, intentAction: String): Intent {
            val intent = Intent()
            intent.action = intentAction
            intent.data = Uri.parse(intentData)
            return intent
        }

        fun smartCreateIntent(intentData: String, intentAction: String) =
                Intent().apply { action = intentAction }
                        .apply { data = Uri.parse(intentData) }

    }
}








