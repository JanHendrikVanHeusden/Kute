package nl.kute.testobjects.java;

import nl.kute.core.AsStringBuilder;
import nl.kute.core.AsStringProducer;

import static nl.kute.core.namedvalues.NamedSupplierKt.namedSupplier;

public class JavaClassWithStatic {
    @SuppressWarnings("ThisEscapedInObjectConstruction")
    private final AsStringProducer producer =
            AsStringBuilder.Companion.asStringBuilder(this)
                    .withAlsoNamed(namedSupplier(() -> JavaClassWithStatic.staticVar, "staticVar"))
                    .build();
    @SuppressWarnings("unused")
    private final String instanceVar = "instance var";
    public static String staticVar = "static var";

    public String toString() {
        return producer.asString();
    }
}
