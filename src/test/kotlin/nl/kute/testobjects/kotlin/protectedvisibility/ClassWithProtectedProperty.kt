package nl.kute.testobjects.kotlin.protectedvisibility

import nl.kute.asstring.core.asString

@Suppress("unused")
open class ClassWithProtectedProperty {

    protected var myProtectedAttribute = "my protected attribute"

    var myPublicAccessibleString = "my public accessible String"

    override fun toString(): String = asString()
}
