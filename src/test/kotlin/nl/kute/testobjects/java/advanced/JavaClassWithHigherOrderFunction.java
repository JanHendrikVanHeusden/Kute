package nl.kute.testobjects.java.advanced;

import java.util.Locale;
import java.util.function.Function;

public class JavaClassWithHigherOrderFunction {
    public Function<String, String> toUpper = (s) -> s.toUpperCase(Locale.ENGLISH);
    public Function<String, String> reverseIt = (s) -> new StringBuffer(s).reverse().toString();
    public Function<Function<String, String>, String> higherOrderFunction =
            (stringFunction) -> stringFunction.apply("a String");
}
