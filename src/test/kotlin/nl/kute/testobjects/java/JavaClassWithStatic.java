package nl.kute.testobjects.java;

import nl.kute.asstring.core.AsStringBuilder;
import nl.kute.asstring.core.AsStringProducer;

import static nl.kute.asstring.namedvalues.NamedSupplierKt.namedSupplier;

@SuppressWarnings("ClassInitializerMayBeStatic")
public class JavaClassWithStatic {

    {
        JavaClassWithStatic.staticVar = "static var";
    }

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
