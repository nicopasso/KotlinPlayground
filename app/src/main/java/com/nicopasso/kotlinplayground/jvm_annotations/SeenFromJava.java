package com.nicopasso.kotlinplayground.jvm_annotations;


public class SeenFromJava {

    MyClass jvmFieldVal = KotlinClass.JVM_FIELD_VAL;

    MyClass jvmFieldVar = KotlinClass.JVM_FIELD_VAR;
    //KotlinClass.JVM_FIELD_VAR = someClass;

    private static void callingFromJava() {

        AFileNameILike.getNON_ANNOTATED_TOP_LEVEL();
        // vs.
        String topLevelAnnotated = AFileNameILike.TOP_LEVEL_ANNOTATED;

        KotlinClass.annotatedFun();
        // vs.
        KotlinClass.Companion.nonAnnotatedFun(); //must go through companion obj

        //Const val
        String constVal = KotlinClass.CONST_VAL;

        //@JvmStatic val
        KotlinClass.getJVM_STATIC_VAL();

        //@JvmStatic var
        KotlinClass.getJVM_STATIC_VAR();
        KotlinClass.setJVM_STATIC_VAR(new MyClass());
    }

}
