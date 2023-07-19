package nl.kute.testobjects.java.advanced;

public class JavaClassWithAnonymousClass {
    @SuppressWarnings({"unused", "Convert2Lambda", "AnonymousInnerClassMayBeStatic", "AnonymousInnerClass"})
    public final AFunctionalInterface propWithAnonymousInnerClass = new AFunctionalInterface() {
        @Override
        public String doSomeThing() {
            return "doing something!";
        }
    };

    public final AFunctionalInterface propWithLambda = () -> "doing some thing!";
}
