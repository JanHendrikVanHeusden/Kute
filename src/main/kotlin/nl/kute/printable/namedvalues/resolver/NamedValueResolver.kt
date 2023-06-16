package nl.kute.printable.namedvalues.resolver

import nl.kute.core.nameToPrint
import nl.kute.printable.namedvalues.NameValue
import nl.kute.printable.namedvalues.PropertyValue
import nl.kute.printable.namedvalues.TypedNameValue

internal fun Any.getNamedValue(it: NameValue<*>) = when (it) {
    is PropertyValue<*, *> -> {
        "${it.name}=${it.valueString}"
    }

    is TypedNameValue<*, *> -> {
        val classPrefix =
            if (it.obj != null && it.obj!!::class != this::class) {
                "${it.obj!!::class.nameToPrint()}."
            } else {
                ""
            }
        "$classPrefix${it.name}=${it.valueString}"
    }

    else -> {
        "${it.name}=${it.valueString}"
    }
}
