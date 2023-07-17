package nl.kute.testobjects.kotlin.protectedvisibility;

public class SubClassOfClassWithProtectedProperty extends ClassWithProtectedProperty {
    @SuppressWarnings("unused")
    public void doSomething() {
        this.getMyProtectedAttribute(); // protected property not directly accessible, only via get method
    }
}
