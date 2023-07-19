package nl.kute.testobjects.kotlin.advanced

class KotlinClassWithHigherOrderFunction {

    var toUpper: (String) -> String = { s -> s.uppercase() }
    var reverseIt: (String) -> String = { s -> StringBuffer(s).reverse().toString() }

    var higherOrderFunction: ((String) -> String) -> String =
        { stringLambda -> stringLambda ("a String") }

}