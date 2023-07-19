package nl.kute.testobjects.java;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class JavaClassWithLambda {
    public final Supplier<Integer> intSupplier = () -> 5;
    public final BiFunction<Integer, Integer, String> intsToString = (i, j) -> String.valueOf(i) + j;
}
