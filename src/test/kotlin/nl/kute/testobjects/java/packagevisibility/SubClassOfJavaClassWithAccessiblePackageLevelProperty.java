package nl.kute.testobjects.java.packagevisibility;

public class SubClassOfJavaClassWithAccessiblePackageLevelProperty extends JavaClassWithPackageLevelProperty {
    @SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
    public void doSomething() {
        this.getMyPackageLevelAttribute(); // to demonstrate that it is accessible
    }
}
