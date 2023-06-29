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
        stringLog = null;
        KuteLog.resetStdOutLogger();
    }

    @Test
    void testLog() {
        // Arrange
        KuteLog.setLogConsumer(createStringLogger());
        String msg = "msg from testLog()";
        // Act
        KuteLog.log(this, msg);
        // Assert
        assertThat(stringLog).isEqualTo(this.getClass() + " - " + msg);
    }

    @Test
    void logShouldAcceptNull() {
        // Arrange
        KuteLog.setLogConsumer(createStringLogger());
        String msg = null;
        // Act
        KuteLog.log(this, msg);
        // Assert
        assertThat(stringLog).isEqualTo(this.getClass() + " - null");
    }

    @Test
    void testLogWithCaller() {
        // Arrange
        KuteLog.setLogConsumer(createStringLogger());
        String msg = "msg from testLog()";
        String caller = "I am the caller";
        // Act
        KuteLog.logWithCaller(caller, msg);
        // Assert
        assertThat(stringLog).isEqualTo(caller + " - " + msg);
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
        assertThat(stringLog).isEqualTo(logMsg2);
    }

    private Consumer<String> createStringLogger() {
        return msg -> stringLog = msg;
    }

    private Consumer<String> createBufferLogger(StringBuffer buffer) {
        return buffer::append;
    }

}
