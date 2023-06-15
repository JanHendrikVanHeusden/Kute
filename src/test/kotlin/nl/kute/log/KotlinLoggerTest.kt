package nl.kute.log

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private var stringLog: String? = null

class KotlinLoggerTest {

    @BeforeEach
    @AfterEach
    fun setUp() {
        stringLog = null
        resetStdOutLogger()
    }

    @Test
    fun `basic test of log method`() {
        // Arrange
        logger = { msg -> stringLog = msg }
        val msg = "msg from testLog()"
        // Act
        this.log(msg)
        // Assert
        assertThat(stringLog).isEqualTo(this::class.toString() + " - " + msg)
    }

    @Test
    fun `basic test of logWithCaller`() {
        // Arrange
        logger = { msg -> stringLog = msg }
        val msg = "msg from testLog()"
        val caller = "I am the caller"
        // Act
        logWithCaller(caller, msg)
        // Assert
        assertThat(stringLog).isEqualTo("$caller - $msg")
    }

    @Test
    fun `test of setting the logger`() {
        // Arrange
        // Buffer to store the log message in
        val logBuffer = StringBuffer()

        // act
        // let it write to buffer
        logger = { msg -> logBuffer.append(msg) }
        val logMsg1 = "Hi from buffered KotlinLoggerTest"
        logger.invoke(logMsg1)

        // assert
        assertThat(logBuffer.toString()).isEqualTo(logMsg1)

        // act
        // let it write to String
        logger = { msg -> stringLog = msg }
        val logMsg2 = "Hi from String KotlinLoggerTest"
        logger.invoke(logMsg2)

        // assert
        assertThat(logBuffer.toString()).isEqualTo(logMsg1) // unchanged
        assertThat(stringLog).isEqualTo(logMsg2)
    }

    @Test
    fun `test that erroneous logger will be rejected`() {
        // Arrange
        // Buffer to store the log message in
        val logBuffer = StringBuffer()
        val goodLogger: (String) -> Unit = { msg -> logBuffer.append(msg) }
        logger = goodLogger
        val testMsg = "This should work"
        log(testMsg)
        assertThat(logBuffer.toString()).endsWith(testMsg)
        assertThat(logger).isSameAs(goodLogger)
        logBuffer.setLength(0) // clear the buffer

        val wrongLogger: (String) -> Unit = { msg -> throw Exception("this is not a good logger") }

        // act
        logger = wrongLogger
        // assert
        assertThat(logger).isSameAs(goodLogger)
        assertThat(logBuffer).contains("logger will not be changed!")
    }

}
