package nl.kute.testobjects.java.advanced;

import java.util.concurrent.Callable;

public class JavaClassWithCallable {
    @SuppressWarnings({"Convert2Lambda", "AnonymousInnerClass", "AnonymousInnerClassMayBeStatic"})
    public Callable<String> myCallable = new Callable<>() {
        @Override
        public String call() {
            return "this is the Callable result";
        }
    };
}
