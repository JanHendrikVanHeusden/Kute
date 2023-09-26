package nl.kute.asstring.filter;

import nl.kute.testobjects.java.filter.PropertyExcludes;
import nl.kute.asstring.property.meta.PropertyMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static nl.kute.asstring.config.AsStringConfigKt.asStringConfig;
import static nl.kute.asstring.core.AsString.asString;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({"LocalVariableNamingConvention", "NewClassNamingConvention"})
class PropertyOmitFilteringTestJava {

    @BeforeEach
    @AfterEach
    @SuppressWarnings("unchecked")
    public void setUpAndTearDown() {
        asStringConfig()
                .withPropertyOmitFilters() // empty, so clears filters
                .applyAsDefault();
    }

    @Test
    void PropertyFiltering_should_exclude_when_specified() {
        // arrange
        PropertyExcludes instance = new PropertyExcludes();
        List<String> propNames = List.of("myListToExclude",
                "myArrayToExclude",
                "myArrayToInclude",
                "myListToInclude",
                "notAList",
                "intToExclude",
                "aNullCollection"
        );
        assertThat(
                Arrays.stream(PropertyExcludes.class.getFields()).map(Field::getName).toArray())
                .containsExactlyInAnyOrderElementsOf(propNames);
        assertThat(
                Arrays.stream(PropertyExcludes.class.getDeclaredFields()).map(Field::getName).toArray())
                .containsExactlyInAnyOrderElementsOf(propNames);

        // act, assert
        assertThat(asString(instance))
                .contains(propNames);

        // arrange
        Predicate<? super PropertyMeta> propFilterByName = meta ->
                meta.getPropertyName().contains("Exclude");
        asStringConfig()
                .withPropertyOmitFilterPredicates(propFilterByName)
                .applyAsDefault();

        // act, assert
        assertThat(asString(instance))
                .contains(propNames.stream()
                        .filter( name -> !name.contains("Exclude"))
                        .collect(Collectors.toList())
                );

        Predicate<? super PropertyMeta> propFilterByCollectionLike =
                PropertyMeta::isCollectionLike;

        asStringConfig()
                .withPropertyOmitFilterPredicates(propFilterByCollectionLike)
                .applyAsDefault();

        List<String> expected = List.of("notAList", "intToExclude");
        List<String> notExpected = propNames.stream()
                .filter(name -> !expected.contains(name))
                .collect(Collectors.toList());

        // act, assert
        assertThat(asString(instance))
                .contains(expected)
                .doesNotContain(notExpected);
    }
}