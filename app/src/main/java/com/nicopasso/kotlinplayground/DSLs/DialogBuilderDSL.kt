package com.nicopasso.kotlinplayground.DSLs

import android.content.DialogInterface
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import com.nicopasso.kotlinplayground.R


fun Fragment.showAlertDialog(cancelable: Boolean = true, buildDialog: AlertDialog.Builder.() -> Unit) {
    nonNullActivity {
        with(AlertDialog.Builder(this)) {
            setCancelable(cancelable)
            buildDialog()
            create().show()
        }
    }
}

fun AlertDialog.Builder.title(title: AlertDialog.Builder.() -> String) {
    setTitle(title())
}

fun AlertDialog.Builder.message(message: AlertDialog.Builder.() -> String) {
    setMessage(message())
}

fun AlertDialog.Builder.positiveButton(
    positiveAction: String = context.getString(R.string.confirm), click: (d: DialogInterface) -> Unit) {
    setPositiveButton(positiveAction) { d, _ ->
        click(d)
        d.dismiss()
    }
}

fun AlertDialog.Builder.negativeButton(
    negativeAction: String = context.getString(R.string.cancel), click: (d: DialogInterface) -> Unit = { it.dismiss() }) {
    setNegativeButton(negativeAction) { d, _ -> click(d) }
}