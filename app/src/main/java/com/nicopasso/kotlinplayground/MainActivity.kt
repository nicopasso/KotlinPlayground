package com.nicopasso.kotlinplayground

import android.graphics.Typeface
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import com.nicopasso.kotlinplayground.conventions.Fraction
import com.nicopasso.kotlinplayground.conventions.get
import com.nicopasso.kotlinplayground.conventions.inc
import java.util.*

//****************************************************
//               EXTENSION FUNCTIONS
//****************************************************
infix fun Int.divideBy(number: Int) = this / number

fun <T> Array<T>.dropFirst() = drop(0)

fun AppCompatActivity.debugLog(message: String) = Log.d(this::javaClass.name, message)

operator fun String.get(range: IntRange): String? {
    var substring: String? = null
    if (range.first >= 0 && range.endInclusive < length) {
        substring = this.substring(range)
    }
    return substring
}

operator fun ClosedRange<Fraction>.iterator() =
        object : Iterator<Fraction> {
            var curr: Fraction = start
            override fun hasNext() = curr <= endInclusive
            override fun next() = curr++
        }
//********************************************************


//********************************************************
//                REIFIED IN ACTION
//********************************************************
/**In an ordinary generic function we can't access the type T because it's ERASED at runtime and
it is ONLY AVAILABLE AT COMPILE TIME! Therefore, if we want to use the generic type as a normal Class
in the function body, we need to EXPLICITLY PASS THE CLASS AS A PARAMETER like c: Class<T> in the example
If we use an inline function with a reified generic type T, the value of T can be accessed even at runtime.*/
fun <T> myGenericFun(c: Class<T>) {}

/** The reified types can only be used in combination with inline functions: inline makes the compiler copy
the function's BYTECODE into every place where the function is being called. When we call an inline function with reified type,
the compiler knows the actual type used as a type argument and MODIFIES THE GENERATED BYTECODE to
 use the corresponding class directly. */
inline fun <reified E> myReifiedGenericFun() {}

/* fun <T> String.toKotlinObject(): T {
    val mapper = jacksonObjectMapper()
    //does not compile
    return mapper.readValue(JsonObject(this).encode(), T::class.java) //Error: Cannot use 'T' as reified parameter.
}*/

/* NO MORE ERRORS AT COMPILE TIME!

inline fun <reified T> String.toKotlinObject(): T {
    val mapper = jacksonObjectMapper()
    return mapper.readValue(JsonObject(this).encode(), T::class.java)
}

usage: "{}".toKotlinObject<MyJsonType>()
*/

//IMPORTANT: inline reified functions are not callable from Java, whereas nomral inline functions are.
//********************************************************

interface Clickable {
    fun click()
    fun showOff() = println("I'm clickable") //so-called "default" implementation
}

interface Focusable {
    fun setFocus(b: Boolean) = println("I ${if (b) "got" else "lost"} focus.")
    fun showOff() = println("I'm focusable")
}

//It's possible to extend a class that implements a specific interface
fun <T : Clickable> T.doubleClick() = println("Double click ${this::javaClass}")

class MainActivity : AppCompatActivity(), Clickable, Focusable {

    override fun showOff() {
        super<Clickable>.showOff()
        super<Focusable>.showOff()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val map = mapOf(1 to "one", 7 to "seven")
        val (number, name) = 1 to "one"
        val newNumber = 7 divideBy randomNum()

        val chars = arrayOf(1, 2, 3)
        chars.dropFirst().all {
            it == chars.first()
        }

        /*******************************************************************
         *
         * with: Logically grouped calls on an object
         * apply: initialization or configuration
         * let: nullability check + conversion of single objects (instead of map)
         * also: anciliary/side effect-y code in chains
         *
         *******************************************************************/
        with(newNumber) {
            //replacement for the Builder pattern (when the class has no builder)
            divideBy(7)
        }

        val dividedBySeven: Int = newNumber.run {
            this divideBy 7
        }

        val dividedByEight: Int = newNumber.let {
            it divideBy 7
            it divideBy 8
        }

        val currentRepo: String? = "hello"
        //Do something when repo is null
        val repo = currentRepo ?: run { initializeRepo() } //nice use of run

        val radioButton = RadioButton(this)
        radioButton createChipWithColor R.color.abc_color_highlight_material

        /*launch(UI) {
            val post = submitPost(10)
            radioButton.text = post
        }*/


        //range
        var greeting = "Hello, world!"
        val comma = greeting.indexOf(",")
        val range: IntRange = comma..greeting.lastIndex
        comma.let {
            greeting = greeting.replaceRange(range, "again")
        }
        debugLog(greeting)

        //Index
        val s = "abcdef"
        for ((i, c) in s.withIndex()) {
            println("$i: $c")
        }

        //*******************************************************
        //                  Fractions
        //*******************************************************
        val sum = Fraction(2, 3) + Fraction(3, 2)
        println("Sum numerator: ${sum[0]}")

        val fracRange = Fraction(1, 5)..Fraction(5, 7)
        println(Fraction(3, 5) in fracRange)

        for (i in fracRange) {
            println("Next: $i")
        }

        //Destructing declarations (it comes by default with a data class)
        val f = Fraction(2, 3)
        val (a, b) = f
        println("Destructed f to: ($a, $b)") //prints "Destructed f to: (2, 3) !!!!!!

        //Invoke
        val frac = Fraction(2, 4)
        frac("My invoke prefix: ") //prints "My invoke prefix: 2/3
        //Translated by the compiler to frac.invoke("My invoke prefix: ")


    }

    /***************************************************************************
     *                        WHICH ONE IS BETTER?                             *
     ***************************************************************************/
    //1
    data class Pin(val creator: String?)
    fun updateAvatar(pin: Pin, button: Button) {
        val creator = pin.creator
        if (creator != null) {
            button.text = creator
        }
    }
    /*Generated code
    public static final void updateAvatar(@NotNull Pin pin, @NotNull Button button) {
        Intrinsics.checkParameterIsNotNull(pin, "pin");
        Intrinsics.checkParameterIsNotNull(button, "button");
        String creator = pin.getCreator();
        if (creator != null) {
            button.setText(creator);
        }
    }*/


    //2
    fun updateAvatar2(pin: Pin, button: Button) {
        pin.creator?.let { //let doesn't have a Java equivalent
            button.text = it
        }
    }
    /*Generated code
    public static final void updateAvatar(@NotNull Pin pin, @NotNull Button button) {
        Intrinsics.checkParameterIsNotNull(pin, "pin");
        Intrinsics.checkParameterIsNotNull(button, "button");
        String var10000 = pin.getCreator();
        if (var10000 != null) {
            String var2 = var10000;
            button.setText(var2);
        }
    }*/

    //If we're using an optimization tool like Proguard, we actually don't care.
    //For us, those 2 versions are equivalent

    /*****************************************************************************/


    fun initializeRepo() {}

    fun todoTest(): List<List<Pair<String, String>>> { TODO() }

    fun randomNum(): Int = Random().nextInt()

    override fun click() = println("Click!")

    val isHello = "hello" IS "hello"

    /*//region COUROUTINES
    suspend fun submitPost(number: Int): String {
        return suspendCoroutine { cont ->
            /*
               makeRequest(number) { result, error ->
                   if (error != null)
                        cont.resumeWithException(error)
                   else
                        cont.resume(result!!)
               }
             */
        }
    }
    //the suspend function is converted by the JVM into:
    // Object submitPost(int number, Continuation<String> cont) { ... }

    public interface Continuation<in T> {
        public val context: CoroutineContext
        public fun resume(value: T)
        public fun resumeWithException(excThrowable: Throwable)
    }
    //endregion*/

    //region Infix methods
    infix fun String.IS(someString: String) = equals(someString)

    infix fun RadioButton.createChipWithColor(@ColorRes bgColor: Int) =
            setText(SpannableString(text.toString().toUpperCase()) applySpanChip this, TextView.BufferType.SPANNABLE)


    infix fun SpannableString.applySpanChip(radioButton: RadioButton) = apply {
        setSpan(StyleSpan(Typeface.BOLD), 0, radioButton.text.length, 0)
        setSpan(RelativeSizeSpan(0.8f), 0, radioButton.text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    //endregion
}

