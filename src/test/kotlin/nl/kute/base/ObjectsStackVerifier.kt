package nl.kute.base

import nl.kute.core.getObjectsStackSize
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

/**
 * Every test class where `asString()` is called, should implement this interface (without any overrides).
 * to verify that the object stack is handled properly.
 *
 * To be exact: it should be empty after every call; so result of [getObjectsStackSize]`()`
 * should always be 0 before and after each test
 *  > To be exact, it must be 0 before and after each call; but we can't easily implement that in a
 *  > test base interface like this one
 */
internal interface ObjectsStackVerifier {

    // If overriding (not advised), make sure to include a call to this super method!
    //
    // Do not clear the objects stack
    // If it would ever fail, we better keep the objects in it for debugging purposes
    @BeforeEach
    fun validateObjectStackBefore() = this.validateObjectsStack()

    // If overriding (not advised), make sure to include a call to this super method!
    //
    // Do not clear the objects stack
    // If it would ever fail, we better keep the objects in it for debugging purposes
    @AfterEach
    fun validateObjectsStackAfter() = this.validateObjectsStack()
}

/**
 * To be called before and after each test, to validate that the object stack is empty before and after.
 *
 * If more fine-grained validation is desired, this validation method can also be called explicitly
 * within tests after each call to `asString()`.
 * This may apply particularly to tests that heavily rely on the objects stack, like objects with
 * self-references or circular mutual references.
 */
internal fun ObjectsStackVerifier.validateObjectsStack() {
    getObjectsStackSize().let { objectStackSize ->
        Assertions.assertThat(objectStackSize)
            .`as`(
                """Before and after every top-level call to `asString()`, `getObjectsStackSize()` should return 0.
               However, a test of ${this::class} finished with `getObjectsStackSize()` = $objectStackSize}"""
                    .trimIndent()
            )
            .isZero
    }
}