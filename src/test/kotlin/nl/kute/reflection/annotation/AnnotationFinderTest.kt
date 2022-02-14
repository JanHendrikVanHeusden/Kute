package nl.kute.reflection.annotation

import nl.kute.printable.Printable
import nl.kute.printable.annotation.PrintOption
import nl.kute.printable.annotation.printOption
import nl.kute.printable.printOption
import nl.kute.reflection.propertiesFromHierarchy
import org.junit.jupiter.api.Test
import kotlin.reflect.KFunction0
import kotlin.reflect.KProperty1


internal class AnnotationFinderTest {

    @PrintOption(maxLength = 200)
    private class Something(@PrintOption(maxLength = 100) val someVal: String): Printable {
        @PrintOption(maxLength = 250)
        override fun toString(): String = asString()
    }

    @Test
    fun findPrintable() {
        val objPrintable = Something("hoi")

        val printOptClassLevel: PrintOption? = objPrintable.printOption()
        println(printOptClassLevel)
        println(printOptClassLevel?.maxLength)

        val printOptPropLevel: PrintOption? = objPrintable::someVal.printOption()
        println(printOptPropLevel)
        println(printOptPropLevel?.maxLength)

        @Suppress("UNCHECKED_CAST")
        val someValProp: List<KProperty1<*, *>> =
            objPrintable.propertiesFromHierarchy(true)
        println(someValProp)

        val toStringMethod: KFunction0<String> = objPrintable::toString
        println(toStringMethod.printOption())
        println(toStringMethod.printOption()?.maxLength)

        TODO("Er moet nog getest worden")
    }
}

