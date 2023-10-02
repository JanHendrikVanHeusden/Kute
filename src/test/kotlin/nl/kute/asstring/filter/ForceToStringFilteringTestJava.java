package nl.kute.asstring.filter;

import nl.kute.asstring.property.meta.ClassMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.Predicate;

import static nl.kute.asstring.config.AsStringConfigKt.asStringConfig;
import static nl.kute.asstring.core.AsString.asString;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NewClassNamingConvention")
public class ForceToStringFilteringTestJava {

    @BeforeEach
    @AfterEach
    @SuppressWarnings("unchecked")
    void setUpAndTearDown() {
        asStringConfig()
                .withForceToStringFilters() // empty, so clears filters
                .applyAsDefault();
    }

    private final String class1ToString = "toString() result of TestClass1";
    private final String class2ToString = "toString() result of TestClass2";

    private class TestClass1 {
        private String prop1;

        @Override
        public String toString() {
            return class1ToString;
        }
    }
    private class TestClass2 {
        private String prop2;

        @Override
        public String toString() {
            return class2ToString;
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void forceToString_filters_should_yield_toString_result_when_applied() {
        // arrange
        TestClass1 testObj1 = new TestClass1();
        TestClass2 testObj2 = new TestClass2();

        assertThat(asString(testObj1))
                .isEqualTo(testObj1.getClass().getSimpleName()+ "(prop1=null)");
        assertThat(asString(testObj2))
                .isEqualTo(testObj2.getClass().getSimpleName()+ "(prop2=null)");

        Predicate<ClassMeta> filter1 = meta ->
                Objects.equals(meta.getObjectClassName(), TestClass1.class.getSimpleName());
        // act
        asStringConfig()
                .withForceToStringFilterPredicates(filter1)
                .applyAsDefault();
        // assert
        assertThat(asString(testObj1))
                .as("Matches filter, so asString() should return toString() result")
                .isEqualTo(class1ToString);
        assertThat(asString(testObj2))
                .as("Does not match filter, so asString() should yield dynamic property resolution")
                .isEqualTo(testObj2.getClass().getSimpleName()+ "(prop2=null)");

        // arrange
        @SuppressWarnings("DataFlowIssue") // because getObjectClassName() *might* return null (it doesn't, in this case)
        Predicate<ClassMeta> filter2 = meta ->
                meta.getObjectClassName().contains("Class2");
        // act
        asStringConfig().withForceToStringFilterPredicates(filter1, filter2).applyAsDefault();

        // assert
        assertThat(asString(testObj1))
                .as("Matches filter, so asString() should return toString() result")
                .isEqualTo(class1ToString);
        assertThat(asString(testObj2))
                .as("Matches filter, so asString() should return toString() result")
                .isEqualTo(class2ToString);

        // act
        asStringConfig().withForceToStringFilterPredicates(filter2).applyAsDefault();

        // assert
        assertThat(asString(testObj1))
                .as("Does not match filter, so asString() should yield dynamic property resolution")
                .isEqualTo(testObj1.getClass().getSimpleName()+ "(prop1=null)");
        assertThat(asString(testObj2))
                .as("Matches filter, so asString() should return toString() result")
                .isEqualTo(class2ToString);
    }
}