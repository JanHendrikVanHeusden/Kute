package nl.kute.asstring.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AsStringWithClassNestedInMethod {

    @Test
    fun `kotlin class nested in method should yield decent output`() {
        // This test is just to demonstrate that with Kotlin, a class nested in method is handled properly.
        //  * With Java this goes wrong (throws KotlinReflectionInternalError),
        //    see AsStringWithClassNestedInMethodJava.java
        class TestClass
        assertThat(TestClass().asString()).isEqualTo("TestClass()")
    }
}