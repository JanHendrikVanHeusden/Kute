package nl.kute.base

import nl.kute.core.clearObjectsStack
import nl.kute.core.getObjectsStackSize
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

/**
 * Every test class where `asString()` is called, should implement this interface (without any overrides).
 * to verify that the object stack is handled properly.
 *
 * To be exact: it should be empty after every call; so [getObjectsStackSize]`()`
 * should always be 0 before and after each test (and to be exact, before and after each call)
 */
interface ObjectsStackVerifier {

    // If overriding (not advised), make sure to call this super method!
    @BeforeEach
    fun setUpObjectStackBase() {
        clearObjectsStack()
        Assertions.assertThat(getObjectsStackSize()).isZero()
    }

    // If overriding (not advised), make sure to call this super method!
    @AfterEach
    fun tearDownObjectStackBaseBase() {
        try {
            this.validateObjectsStack()
        } finally {
            clearObjectsStack()
        }
    }
}

/**
 * To be called after each test.
 *
 * If more fine-grained validation is desired, it can be called after each call to `asString()` with tests.
 */
internal fun Any.validateObjectsStack() {
    getObjectsStackSize().let { objectStackSize ->
        Assertions.assertThat(objectStackSize)
            .`as`(
                """After every call to `asString()`, `getObjectsStackSize()` should return 0.
               However, a test of ${this::class} finished with `getObjectsStackSize()` = $objectStackSize}"""
                    .trimIndent()
            )
            .isZero()
    }
}