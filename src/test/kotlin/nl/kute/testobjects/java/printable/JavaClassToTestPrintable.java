package nl.kute.testobjects.java.printable;

import nl.kute.core.Printable;
import org.jetbrains.annotations.NotNull;

import static nl.kute.core.AsStringBuilder.asStringBuilder;

/** For use by [PrintableTest] */
@SuppressWarnings("unused")
public class JavaClassToTestPrintable implements Printable {

    private String str;
    private int num;
    @SuppressWarnings("FieldCanBeLocal")
    private final String[] names;

    public JavaClassToTestPrintable(String str, int num, String... names) {
        this.str = str;
        this.num = num;
        this.names = names;
    }

    @Override
    public @NotNull String toString() {
        return asString();
    }

    public String testIt() {
        return asString();
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
        JavaClassToTestPrintable testObj = new JavaClassToTestPrintable(" the string ", 15, "Henk");
    }
}
