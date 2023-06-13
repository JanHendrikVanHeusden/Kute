package nl.kute.testobjects.java.printable.packagevisibility;

import static nl.kute.core.PrintableCore.asString;

@SuppressWarnings("unused")
public class JavaClassWithPackageLevelProperty {

    private String myPackageLevelAttribute = "my package level attribute";

    public String myPublicAccessibleString = "my public accessible String";

    @SuppressWarnings("UnusedReturnValue")
    String getMyPackageLevelAttribute() {
        return myPackageLevelAttribute;
    }

    void setMyPackageLevelAttribute(String myPackageLevelAttribute) {
        this.myPackageLevelAttribute = myPackageLevelAttribute;
    }

    @Override
    public String toString() {
        return asString(this);
    }

}
