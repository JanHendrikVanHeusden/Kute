package nl.kute.testobjects.java.printable.protectedvisibility;

import static nl.kute.core.PrintableCore.asString;

@SuppressWarnings("unused")
public class JavaClassWithProtectedProperty {

    protected String myProtectedAttribute = "my protected attribute";

    public String myPublicAccessibleString = "my public accessible String";

    @SuppressWarnings("UnusedReturnValue")
    protected String getMyProtectedAttribute() {
        return myProtectedAttribute;
    }

    protected void setMyProtectedAttribute(String myProtectedAttribute) {
        this.myProtectedAttribute = myProtectedAttribute;
    }

    @Override
    public String toString() {
        return asString(this);
    }

}
