package nl.kute.log;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("KotlinInternalInJava")
public class JavaLoggerTest {

    static String stringLog;

    @BeforeEach
    @AfterEach
    void setUp() {
        JavaLoggerTest.stringLog = null;
        KuteLog.resetStdOutLogger();
    }

    @Test
    void log_shouldRenderCallerAndMessage() {
        // Arrange
        KuteLog.setLogConsumer(createStringLogger());
        String msg = "msg from testLog()";
        // Act
        KuteLog.log(this, msg);
        // Assert
        assertThat(JavaLoggerTest.stringLog).isEqualTo(this.getClass() + " - " + msg);
    }

    @Test
    void log_shouldAcceptNull() {
        // Arrange
        KuteLog.setLogConsumer(createStringLogger());
        String msg = null;
        // Act
        KuteLog.log(this, msg);
        // Assert
        assertThat(JavaLoggerTest.stringLog).isEqualTo(this.getClass() + " - null");
    }

    @Test
    void setLogger() {
        // Arrange
        // Buffer to store the log message in
        StringBuffer buffer = new StringBuffer();

        // act
        KuteLog.setLogConsumer(createBufferLogger(buffer));
        String logMsg1 = "Hi from buffered JavaLoggerTest";
        KuteLog.getLogger().invoke(logMsg1);

        // assert
        assertThat(buffer.toString()).isEqualTo(logMsg1);

        // act
        KuteLog.setLogConsumer(createStringLogger()); // will store it in stringLog
        String logMsg2 = "Hi from String JavaLoggerTest";
        KuteLog.getLogger().invoke(logMsg2);

        // assert
        assertThat(buffer.toString()).isEqualTo(logMsg1); // unchanged
        assertThat(JavaLoggerTest.stringLog).isEqualTo(logMsg2);
    }

    private Consumer<String> createStringLogger() {
        return msg -> JavaLoggerTest.stringLog = msg;
    }

    private Consumer<String> createBufferLogger(StringBuffer buffer) {
        return buffer::append;
    }

}
