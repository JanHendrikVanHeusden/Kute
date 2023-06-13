package nl.kute.testobjects.java.printable.protectedvisibility;

public class SubClassOfJavaClassWithProtectedProperty extends JavaClassWithProtectedProperty {
    @SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
    public void doSomething() {
        this.getMyProtectedAttribute();
    }
}
