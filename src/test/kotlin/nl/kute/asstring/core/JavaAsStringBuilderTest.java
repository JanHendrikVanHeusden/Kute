package nl.kute.asstring.core;

import kotlin.jvm.functions.Function0;
import kotlin.reflect.KProperty;
import nl.kute.asstring.annotation.modify.AsStringHash;
import nl.kute.asstring.annotation.modify.AsStringOmit;
import nl.kute.asstring.annotation.option.PropertyValueSurrounder;
import nl.kute.asstring.config.AsStringConfig;
import nl.kute.asstring.namedvalues.NamedProp;
import nl.kute.asstring.namedvalues.NamedSupplier;
import nl.kute.asstring.namedvalues.NamedValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.Callable;

import static nl.kute.asstring.core.AsString.asString;
import static nl.kute.helper.helper.AssertionHelper.containsExhaustiveInAnyOrderCondition;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The tests in this class are intended to demonstrate - rather than as exhaustive test - that
 * {@link AsString#asString(Object)} and {@link AsStringBuilder} can be used from within Java.
 * <p>
 * <br>
 * Kotlin specific types, e.g. {@link kotlin.reflect.KProperty} can not be used however,
 * which also excludes usage of, for instance, {@link NamedProp},
 * and of {@link AsStringBuilder#withOnlyProperties(KProperty[])} etc.
 * <br>
 * So to summarize: usage is limited to the types that are available in Java.
 * </p>
 */
@SuppressWarnings("DuplicateStringLiteralInspection")
class JavaAsStringBuilderTest {

    @BeforeEach
    @AfterEach
    public void setUpAndTearDown() {
        new AsStringConfig()
                .withSurroundPropValue(PropertyValueSurrounder.NONE)
                .withIncludeIdentityHash(false)
                .applyAsDefault();
    }

// region ~ Test objects

    @SuppressWarnings("unused")
    private static class TestClass {
        protected final String prop1 = "I am prop1";
        protected final String prop2 = "I am prop2";
        private final String prop3 = "I am prop3";
        private final String prop4 = "I am prop4";
        @AsStringOmit
        public final String omitted = "I will be omitted";
        @AsStringHash
        public final String hashed = "I will be hashed";
    }

// endregion

// region ~ Tests

    @Test
    void asStringFromJava() {
        // arrange
        String[] expectedPropStrings = {
                "prop1=I am prop1",
                "prop2=I am prop2",
                "prop3=I am prop3",
                "prop4=I am prop4",
                "hashed=#cb1a6ec1#"
        };
        // act
        String asString = asString(new TestClass());
        // assert
        assertThat(asString)
                .is(containsExhaustiveInAnyOrderCondition(
                                expectedPropStrings,
                                ", ",
                                "TestClass(",
                                ")"
                        )
                );
    }

    @Test
    void asStringWithConfigOptionsFromJava() {
        // arrange
        new AsStringConfig()
                .withIncludeIdentityHash(true)
                .withSurroundPropValue(PropertyValueSurrounder.GUILLEMETS)
                .applyAsDefault();
        String[] expectedPropStrings = {
                "prop1=«I am prop1»",
                "prop2=«I am prop2»",
                "prop3=«I am prop3»",
                "prop4=«I am prop4»",
                "hashed=«#cb1a6ec1#»"
        };
        TestClass testObj = new TestClass();
        String identityHashHex = Long.toHexString(System.identityHashCode(testObj));
        // act
        String asString = asString(testObj);
        // assert
        assertThat(asString)
                .is(containsExhaustiveInAnyOrderCondition(
                                expectedPropStrings,
                                ", ",
                                "TestClass@" + identityHashHex + "(",
                                ")"
                        )
                );
    }
    @Test
    void asStringBuilderFromJava() {
        // arrange
        long random = new Random().nextLong();
        String[] expectedPropStrings = {
                "prop1=I am prop1",
                "prop2=I am prop2",
                "prop4=I am prop4",
                "hashed=#cb1a6ec1#",
                "wouldBeOmitted=I will be omitted",
                "a random=" + random
        };
        TestClass testObj = new TestClass();

        // act
        NamedValue<Long> namedValue = new NamedValue<>("a random", random);

        // to use NamedSupplier, one has to define this as a variable
        // either of type Function0, or of type Callable
        NamedSupplier<String> namedSupplier =
                new NamedSupplier<>("wouldBeOmitted", (Function0<String>) () -> testObj.omitted);

        AsStringProducer asStringProducer =
                AsStringBuilder.asStringBuilder(testObj)
                        .exceptPropertyNames("prop3")
                        .withAlsoNamed(namedSupplier, namedValue)
                        .build();

        // assert
        assertThat(asStringProducer.asString())
                .is(containsExhaustiveInAnyOrderCondition(
                                expectedPropStrings,
                                ", ",
                                "TestClass(",
                                ")"
                        )
                );
    }

    @Test
    void asStringBuilderFromJava_with_supplier_as_Callable() {
        // arrange
        long random = new Random().nextLong();
        String[] expectedPropStrings = {
                "prop1=I am prop1",
                "prop2=I am prop2",
                "prop4=I am prop4",
                "hashed=#cb1a6ec1#",
                "wouldBeOmitted=I will be omitted",
                "a random=" + random
        };
        TestClass testObj = new TestClass();

        // act
        NamedValue<Long> namedValue = new NamedValue<>("a random", random);
        // to use NamedSupplier, one has to define this as a variable
        // either of type Function0, or of type Callable
        Callable<String> stringCallable = () -> testObj.omitted;
        NamedSupplier<String> namedSupplier =
                new NamedSupplier<>("wouldBeOmitted", stringCallable);
        assertThat(namedSupplier.getValue()).isNotNull();

        AsStringProducer asStringProducer =
                AsStringBuilder.asStringBuilder(testObj)
                        .exceptPropertyNames("prop3")
                        .withAlsoNamed(namedSupplier, namedValue)
                        .build();

        // assert
        assertThat(asStringProducer.asString())
                .is(containsExhaustiveInAnyOrderCondition(
                                expectedPropStrings,
                                ", ",
                                "TestClass(",
                                ")"
                        )
                );
    }

// endregion

}
