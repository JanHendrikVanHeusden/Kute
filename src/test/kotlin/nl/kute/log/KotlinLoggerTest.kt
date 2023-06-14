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
    fun testLog() {
        // Arrange
        logger = { msg -> stringLog = msg }
        val msg = "msg from testLog()"
        // Act
        this.log(msg)
        // Assert
        assertThat(stringLog).isEqualTo(this::class.toString() + " - " + msg)
    }

    @Test
    fun testLogWithCaller() {
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
    fun setLogger() {
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

}
