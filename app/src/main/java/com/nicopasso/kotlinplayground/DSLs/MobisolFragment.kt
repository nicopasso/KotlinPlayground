package com.nicopasso.kotlinplayground.DSLs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.nicopasso.kotlinplayground.R
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.UI

/*********************************************************************************************************
 *
 *  ___     ___     _
 * |   \   / __|   | |      ___
 * | |) |  \__ \   | |__   (_-<
 * |___/   |___/   |____|  /__/_
 * _|"""""|_|"""""|_|"""""|_|"""""|
 * "`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'
 *
 * A domain-specific language (DSL) is a computer language specialized to a particular application domain
 * In our daily basis development we use DSLs already when we write Gradle tasks.
 *

 *
 * The Kotlin community already offers a big number of libraries that use the DSL approach
 * 1) Anko
 * 2) Kotlintest
 * 3) mockk
 * 4) Koin
 *
 *
 * Kotlin provides several ways to create a cleaner syntax:
 * 1) LAMBDAS outside of method parentheses
 * 2) EXTENSION FUNCTIONS
 * 3) LAMBDA WITH RECEIVERS
 *
 * A good place to use a DSL could be a LIBRARY INTERFACE, for example, when the user doesn't
 * have to know how the Model is built.
 * However, sometimes a DSL could be useful when we just want to avoid code repetition
 * and we want to improve the syntax not just for future ourselves but also for our colleagues
 * (Code Reviews, further development of a feature).
 *
 *********************************************************************************************************/


class DSLFragment: Fragment() {

    private val presenter: DSLPresenter = DSLPresenter()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openFirstAlertDialogWithDSLs()
        openSecondAlertDialogWithDSLs()
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        //ANKO
        val customStyle = { v: Any ->
            when (v) {
                is Button -> v.textSize = 26f
                is EditText -> v.textSize = 24f
            }
        }

        return UI {

            verticalLayout {
                padding = dip(32)

                imageView().lparams {
                    margin = dip(16)
                    gravity = Gravity.CENTER
                }

                editText {
                    hintResource = R.string.password
                    inputType = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD
                }

                button("Log in") {
                    onClick {

                    }
                    onClick {
                        // Do something
                    }
                }
            }
        }.apply(customStyle).view
    }















    /*
        ALERT DIALOG - THE NORMAL WAY
     */
    private fun openFirstDialogBasic() {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle("Beautiful AlertDialog")
                .setMessage("Congrats! You built your first AlertDialog with DSLs")
                .setPositiveButton(getString(R.string.back_home)) { d, _ ->
                    d.dismiss()
                    it.finish()
                }
                .create()
                .show()
        }
    }

    private fun openSecondDialogBasic() {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.welcome))
                .setMessage("Another beautiful message")
                .setPositiveButton(getString(R.string.confirm)) { d, _ ->
                    presenter.doSomething()
                    d.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { d, _ ->
                    d.dismiss()
                }
                .create()
                .show()
        }
    }




























    /*
        USE OUR OWN DSLs
     */
    private fun openFirstAlertDialogWithDSLs() {

        showAlertDialog {

            title { "Beautiful AlertDialog" }

            message { "Congrats! You built your first AlertDialog with DSLs" }

            positiveButton {
                nonNullActivity { finish() }
            }
        }
    }


    private fun openSecondAlertDialogWithDSLs() {

        showAlertDialog {

            title { getString(R.string.welcome) }

            message { "Another beautiful message" }

            positiveButton {
                presenter.doSomething()
            }

            negativeButton() //dismiss is done as the default behavior

        }
    }

}

















class DSLPresenter {
    fun doSomething() {
        //stuff
    }
}