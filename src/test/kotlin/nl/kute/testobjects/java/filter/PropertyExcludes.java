package nl.kute.testobjects.java.filter;

import kotlin.random.Random;

import java.util.Collection;
import java.util.List;

import static nl.kute.asstring.core.AsString.asString;

@SuppressWarnings({"unused", "PublicField"}) // properties read reflectively
public class PropertyExcludes {
    public List<String> myListToExclude = List.of("I", "will", "be", "excluded");
    public String[] myArrayToExclude = {"I", "will", "also", "be", "excluded"};
    public String[] myArrayToInclude = {"I", "will", "be", "included"};
    public List<String> myListToInclude = List.of("I", "will", "also", "be", "included");
    public Collection<Object> aNullCollection = null;
    public String notAList = "not a list";
    public int intToExclude = Random.Default.nextInt();

    public String toString() {
        return asString(this);
    }
}
