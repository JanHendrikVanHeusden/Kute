package nl.kute.testobjects.performance.java;

import com.google.gson.Gson;
import kotlin.reflect.KClass;
import kotlin.reflect.KMutableProperty1;
import nl.kute.testobjects.performance.PropsToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static nl.kute.asstring.core.AsString.asString;
import static nl.kute.performance.PerformanceTestHelperKt.javaClassToKotlinClass;
import static nl.kute.performance.PerformanceTestHelperKt.retrieveProperties;

@SuppressWarnings({"unused", "FieldMayBeFinal", "UseOfObsoleteDateTimeApi",
        "MissingJavadoc", "FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection",
        "HardCodedStringLiteral", "UnqualifiedStaticUsage", "WeakerAccess"})
public class TestClassesFewJavaVars {

    private static final Double[] emptyArrayDouble = {};
    private static final int[] emptyArrayInt = {};

    public static Set<KClass<? extends PropsToString>> propClassesFewJavaVars = new HashSet<>();

    static {
        propClassesFewJavaVars.addAll(Arrays.asList(
                javaClassToKotlinClass(FewJavaVarsSub00.class),
                javaClassToKotlinClass(FewJavaVarsSub02.class),
                javaClassToKotlinClass(FewJavaVarsSub03.class),
                javaClassToKotlinClass(FewJavaVarsSub04.class),
                javaClassToKotlinClass(FewJavaVarsSub01.class),
                javaClassToKotlinClass(FewJavaVarsSub05.class),
                javaClassToKotlinClass(FewJavaVarsSub06.class),
                javaClassToKotlinClass(FewJavaVarsSub07.class),
                javaClassToKotlinClass(FewJavaVarsSub08.class),
                javaClassToKotlinClass(FewJavaVarsSub09.class),
                javaClassToKotlinClass(FewJavaVarsSub10.class),
                javaClassToKotlinClass(FewJavaVarsSub11.class),
                javaClassToKotlinClass(FewJavaVarsSub12.class),
                javaClassToKotlinClass(FewJavaVarsSub13.class),
                javaClassToKotlinClass(FewJavaVarsSub14.class),
                javaClassToKotlinClass(FewJavaVarsSub15.class),
                javaClassToKotlinClass(FewJavaVarsSub16.class),
                javaClassToKotlinClass(FewJavaVarsSub17.class),
                javaClassToKotlinClass(FewJavaVarsSub18.class),
                javaClassToKotlinClass(FewJavaVarsSub19.class)
        ));
    }

    public static final List<KMutableProperty1<? extends PropsToString, Object>> propListFewJavaVarsAll =
            retrieveProperties(propClassesFewJavaVars);

    public static final List<PropsToString> testObjectsFewJavaVars = new ArrayList<PropsToString>(1000);

    static {
        for (int i = 0; i < 100; i++) {
            testObjectsFewJavaVars.add(new FewJavaVarsSub00());
            testObjectsFewJavaVars.add(new FewJavaVarsSub01());
            testObjectsFewJavaVars.add(new FewJavaVarsSub02());
            testObjectsFewJavaVars.add(new FewJavaVarsSub03());
            testObjectsFewJavaVars.add(new FewJavaVarsSub04());
            testObjectsFewJavaVars.add(new FewJavaVarsSub05());
            testObjectsFewJavaVars.add(new FewJavaVarsSub06());
            testObjectsFewJavaVars.add(new FewJavaVarsSub07());
            testObjectsFewJavaVars.add(new FewJavaVarsSub08());
            testObjectsFewJavaVars.add(new FewJavaVarsSub09());
            testObjectsFewJavaVars.add(new FewJavaVarsSub10());
            testObjectsFewJavaVars.add(new FewJavaVarsSub11());
            testObjectsFewJavaVars.add(new FewJavaVarsSub12());
            testObjectsFewJavaVars.add(new FewJavaVarsSub13());
            testObjectsFewJavaVars.add(new FewJavaVarsSub14());
            testObjectsFewJavaVars.add(new FewJavaVarsSub15());
            testObjectsFewJavaVars.add(new FewJavaVarsSub16());
            testObjectsFewJavaVars.add(new FewJavaVarsSub17());
            testObjectsFewJavaVars.add(new FewJavaVarsSub18());
            testObjectsFewJavaVars.add(new FewJavaVarsSub19());
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars00 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub00 extends FewJavaVars00 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars01 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub01 extends FewJavaVars01 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars02 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub02 extends FewJavaVars02 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars03 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub03 extends FewJavaVars03 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars04 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub04 extends FewJavaVars04 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars05 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub05 extends FewJavaVars05 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars06 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub06 extends FewJavaVars06 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars07 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub07 extends FewJavaVars07 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars08 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub08 extends FewJavaVars08 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars09 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub09 extends FewJavaVars09 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars10 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub10 extends FewJavaVars10 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars11 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub11 extends FewJavaVars11 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars12 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub12 extends FewJavaVars12 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars13 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub13 extends FewJavaVars13 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars14 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub14 extends FewJavaVars14 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars15 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub15 extends FewJavaVars15 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars16 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub16 extends FewJavaVars16 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars17 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub17 extends FewJavaVars17 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars18 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub18 extends FewJavaVars18 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

    @State(Scope.Thread)
    public static class FewJavaVars19 extends PropsToString {
        public String propS = "";
        public int propI = 0;
        public Object propAny = new Object();
        public List<String> propLs = new ArrayList<>();
        public Date propD = new Date();

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withAsString() {
            return asString(this);
        }

        @NotNull
        @Override
        public String withGson() {
            return gson.toJson(this);
        }

        @NotNull
        @Override
        public String withToStringBuilder() {
            return ToStringBuilder.reflectionToString(this);
        }

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ")";
        }
    }


    @State(Scope.Thread)
    public static class FewJavaVarsSub19 extends FewJavaVars19 {

        private static final Double[] propAd = TestClassesFewJavaVars.emptyArrayDouble;
        private static final int[] propAi = emptyArrayInt;

        private static Gson gson = new Gson();

        @NotNull
        @Override
        public String withIdeGeneratedToString() {
            return this.getClass().getSimpleName()
                    + "(propS='" + propS
                    + "', propI=" + propI
                    + ", propAny=" + propAny
                    + ", propLs=" + propLs
                    + ", propD=" + propD
                    + ", propAs={" + Arrays.toString(propAd)
                    + ", propAi={" + Arrays.toString(propAi)
                    + "})";
        }
    }

}
