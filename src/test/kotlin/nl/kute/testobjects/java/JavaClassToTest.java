package nl.kute.testobjects.java;

import org.jetbrains.annotations.NotNull;

import static nl.kute.core.AsString.asString;

/** For use by [PrintableTest] */
@SuppressWarnings("unused")
public class JavaClassToTest {

    private String str;
    private int num;
    @SuppressWarnings("FieldCanBeLocal")
    private final String[] names;

    public JavaClassToTest(String str, int num, String... names) {
        this.str = str;
        this.num = num;
        this.names = names;
    }

    @Override
    @NotNull
    public String toString() {
        return asString(this);
    }

    public String testIt() {
        return asString(this);
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public static void main(String[] args) {
        JavaClassToTest testObj = new JavaClassToTest(" the string ", 15, "Henk");
    }
}
