package nl.kute.asstring.java;

import nl.kute.testobjects.java.asstringannotations.WithAnnotations;
import org.junit.jupiter.api.Test;

import static nl.kute.asstring.core.AsString.asString;
import static org.assertj.core.api.Assertions.assertThat;

class AsStringAnnotationJavaTest {

    @Test
    void testAsStringAnnotationsInJava() {
        WithAnnotations testObj = new WithAnnotations();
        assertThat(asString(testObj))
                .startsWith(testObj.getClass().getSimpleName())
                .contains(
                        "toBeReplaced=My *** should be replaced",
                        "toBeHashed=#cb1a6ec1#", // CRC32C hash of "I will be hashed"
                        "withBackTicks=`I will be surrounded by backticks`"
                        );
    }
}
