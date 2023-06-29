package nl.kute.testobjects.java.packagevisibility.sub;

import nl.kute.testobjects.java.packagevisibility.JavaClassWithPackageLevelProperty;

public class SubClassOfJavaClassWithNotAccessiblePackageLevelProperty extends JavaClassWithPackageLevelProperty {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void doSomething() {
    }
}
