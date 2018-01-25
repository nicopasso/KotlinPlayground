@file:JvmName("AFileNameILike")
package com.nicopasso.kotlinplayground.jvm_annotations

val NON_ANNOTATED_TOP_LEVEL = "Top Level"
@JvmField val TOP_LEVEL_ANNOTATED = "Top Level Field"


//Look at the SeenFromJava.java class to see the effects of Jvm annotations
class KotlinClass {
    companion object {
        /*
            1) JvmField annotation:
            Creates a public static field that is directly accessed

            Implemented like:
            public static MyClass JVM_FIELD_VAR = new MyClass();
         */
        @JvmField
        val JVM_FIELD_VAL = MyClass()
        @JvmField
        var JVM_FIELD_VAR = MyClass()

        @JvmStatic
        fun annotatedFun() {}

        fun nonAnnotatedFun() {}

        /*
            2) Const keywords:
            they can only be used with a primitive or String value
         */
        const val CONST_VAL = "const val"
        const val CONST_VAL_INT = 5
        const val CONST_VAL_FLOAT = 5f
        //const val CONST_NO_NULLS: String? = null //Nope: no nulls
        //const val CONST_CLASS_FAILS = MyClass() //Nope: no classes
        //const var CONST_VAR = "const var" //nope: no vars

        /*
            3) JvmStatic annotation
            Creates a private static field accessed via public static get/setters

            Implemented like:
            private static final MyClass JVM_STATIC VAL = new MyClass();
            public static final MyClass getJVM_STATIC_VAL() { ... }
         */
        @JvmStatic
        val JVM_STATIC_VAL = MyClass()
        @JvmStatic
        var JVM_STATIC_VAR = MyClass()
    }
}

class MyClass