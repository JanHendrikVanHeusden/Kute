package nl.kute.testobjects.java;

import nl.kute.core.AsStringBuilder;
import nl.kute.core.AsStringProducer;

import static nl.kute.core.namedvalues.NamedSupplierKt.namedVal;

public class JavaClassWithStatic {
    private final AsStringProducer producer =
            AsStringBuilder.Companion.asStringBuilder(this)
                    .withAlsoNamed(namedVal(() -> staticVar, "staticVar"))
                    .build();
    @SuppressWarnings("unused")
    private final String instanceVar = "instance var";
    public static String staticVar = "static var";

    public String toString() {
        return producer.asString();
    }
}
