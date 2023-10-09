package nl.kute.asstring.core;

import org.junit.jupiter.api.Test;

import static nl.kute.asstring.core.AsString.asString;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NewClassNamingConvention")
public class AsStringWithClassNestedInMethodJava {

    @Test
    void java_class_nested_in_method_should_log_but_not_cause_Error() {
        // arrange
        // Known issue: Kotlin's reflection can not handle classes that are nested in methods,
        // (throws KotlinReflectionInternalError)
        class TestClass {
            // empty
        }
        TestClass testObj = new TestClass();
        String identityHashHex = Integer.toHexString(System.identityHashCode(testObj));

        // act, assert
        assertThat(asString(testObj)).isEqualTo("TestClass@" + identityHashHex );
    }
}
