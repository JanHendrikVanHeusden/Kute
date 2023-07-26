package nl.kute.testobjects.java;

@SuppressWarnings({"UtilityClassCanBeEnum", "PublicStaticArrayField"})
public final class JavaClassWithComposites {
    // do not instantiate
    private JavaClassWithComposites() {
        //
    }

    public static final char[] ARRAY_OF_CHAR_PRIMITIVES = {'1', 'Æ'};
    public static final byte[] ARRAY_OF_BYTE_PRIMITIVES = {(byte) 1, (byte) 127, (byte) -128};

    public static final Character[] ARRAY_OF_CHARACTER = {'X', '1', 'Æ'};
    public static final Byte[] ARRAY_OF_BYTE_OBJECTS = {1, 127, -128};
}
