package nl.kute.testobjects.java.asstringannotations;

import nl.kute.asstring.annotation.modify.AsStringHash;
import nl.kute.asstring.annotation.modify.AsStringOmit;
import nl.kute.asstring.annotation.modify.AsStringReplace;
import nl.kute.asstring.annotation.option.AsStringOption;
import nl.kute.asstring.annotation.option.PropertyValueSurrounder;

@SuppressWarnings({"unused", "FieldMayBeStatic"})
public class WithAnnotations {
    @AsStringOmit
    private final String toBeOmitted = "I should be omitted";

    @AsStringReplace(pattern = "value", replacement = "***", isRegexpPattern = false)
    private final String toBeReplaced = "My value should be replaced";

    @AsStringHash
    private final String toBeHashed = "I will be hashed";

    @AsStringOption(surroundPropValue = PropertyValueSurrounder.BACKTICKS)
    private final String withBackTicks = "I will be surrounded by backticks";

}
