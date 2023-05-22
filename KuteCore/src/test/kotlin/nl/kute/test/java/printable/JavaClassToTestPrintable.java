package nl.kute.test.java.printable;

/** For use by [PrintableTest] */
@SuppressWarnings("unused")
public class JavaClassToTestPrintable {

    private String str;
    private int num;
    @SuppressWarnings("FieldCanBeLocal")
    private final String[] names;

    public JavaClassToTestPrintable(String str, int num, String[] names) {
        this.str = str;
        this.num = num;
        this.names = names;
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

}
