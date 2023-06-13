package nl.kute.testobjects.java.printable.packagevisibility;

public class SubClassOfJavaClassWithAccessiblePackageLevelProperty extends JavaClassWithPackageLevelProperty {
    @SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
    public void doSomething() {
        this.getMyPackageLevelAttribute(); // to demonstrate that it is accessible
    }
}
