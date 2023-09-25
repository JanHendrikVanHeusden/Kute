package nl.kute.exception

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.concurrent.CancellationException
import java.util.stream.Stream
import kotlin.test.fail

class ExceptionHandlingTest {

    private var actionInvoked = false
    private val valueToReturnOnException: String = "the value to return on Exceptions"

    @BeforeEach
    fun setUp() {
        actionInvoked = false
    }

    @Test
    fun `handleException should handle catchable Exceptions and perform the onException action`() {
        tryIt(Exception("Boom!")) // should not throw
        assertThat(actionInvoked).isTrue()
    }

    @Test
    fun `handleWithReturn should handle catchable Exceptions, perform the onException action, and return the value provided`() {
        val result = tryItWithReturn(Exception("Boom!")) // should not throw
        assertThat(result).isSameAs(valueToReturnOnException)
        assertThat(actionInvoked).isTrue()
    }

    @ParameterizedTest
    @ArgumentsSource(UncaughtThrowableProvider::class)
    fun `handleException should (re)throw not-to-catch Throwable and skip the onException action`(t: Throwable) {
        // arrange
        val toTest: Map<String, Action> = mapOf("handleException()" to { tryIt(t) }, "handleWithReturn()" to { tryItWithReturn(t) })

        toTest.forEach {
            actionInvoked = false
            val method = it.key
            val toExecute = it.value

            // act, assert
            val thrown = assertThrows<Throwable> { toExecute() }

            // The handlers should be inline, and should rethrow these without wrapping it in some other Exception,
            // so stack trace should not be cluttered. Let's test it!
            val testClassName = this::class.simpleName!!
            val targetClassName: String = Class.forName("nl.kute.exception.ExceptionHandling").simpleName
            assertThat(thrown.stackTraceToString().replace(testClassName, ""))
                .doesNotContain(targetClassName)
                .doesNotContain(method.replace("()", ""))

            // assert
            when(thrown) {
                is InterruptedException -> {
                    assertThat(actionInvoked).`as`("$method failed").isFalse
                }
                is CancellationException -> {
                    assertThat(actionInvoked).`as`("$method failed").isFalse
                }
                is Error -> {
                    assertThat(actionInvoked).`as`("$method failed").isFalse
                }
                else -> {
                    if (thrown::class == Throwable::class && thrown !is Exception) {
                        assertThat(actionInvoked).`as`("$method failed").isFalse
                    }
                    else {
                        // should not happen
                        fail("Unexpected Throwable:\n ${t.throwableAsString()}")
                    }
                }
            }
        }
    }

// region ~ Test helper stuff

    private fun tryIt(t: Throwable): Unit = try {
        throw t
    } catch (e: Exception) {
        handleException(e) {
            actionInvoked = true
        }
    }

    private fun tryItWithReturn(t: Throwable): String = try {
        throw t
    } catch (e: Exception) {
        handleWithReturn(e, "the value to return on Exceptions") {
            actionInvoked = true
        }
    }

    private class UncaughtThrowableProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<out Arguments?> {
            return Stream.of(
                Arguments.of(InterruptedException()),
                Arguments.of(CancellationException()),
                Arguments.of(Error()),
                Arguments.of(Throwable()),
            )
        }
    }

}

// endregion