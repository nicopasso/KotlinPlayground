package com.nicopasso.kotlinplayground.DSLs

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity


fun Fragment.nonNullActivity(block: FragmentActivity.() -> Unit) {
    activity?.block()
}