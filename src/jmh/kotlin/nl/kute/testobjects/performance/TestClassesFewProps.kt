@file:Suppress("KDocMissingDocumentation")

package nl.kute.testobjects.performance

import nl.kute.asstring.core.asString
import nl.kute.performance.retrieveProperties
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.builder.ToStringBuilder
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import java.util.Date
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties

val propClassesFewProps: Set<KClass<out PropsToString>> = setOf(FewPropsSub00::class,
    FewPropsSub01::class,
    FewPropsSub02::class,
    FewPropsSub03::class,
    FewPropsSub04::class,
    FewPropsSub05::class,
    FewPropsSub06::class,
    FewPropsSub07::class,
    FewPropsSub08::class,
    FewPropsSub09::class,
    FewPropsSub10::class,
    FewPropsSub11::class,
    FewPropsSub12::class,
    FewPropsSub13::class,
    FewPropsSub14::class,
    FewPropsSub15::class,
    FewPropsSub16::class,
    FewPropsSub17::class,
    FewPropsSub18::class,
    FewPropsSub19::class
)

val propListFewPropsAll: List<KMutableProperty1<out PropsToString, *>> =
    propClassesFewProps.retrieveProperties()

val testObjectsFewProps: List<PropsToString> = ArrayList<PropsToString>(1000).also { list ->
    repeat(100) {
        list.add(FewPropsSub00())
        list.add(FewPropsSub01())
        list.add(FewPropsSub02())
        list.add(FewPropsSub03())
        list.add(FewPropsSub04())
        list.add(FewPropsSub05())
        list.add(FewPropsSub06())
        list.add(FewPropsSub07())
        list.add(FewPropsSub08())
        list.add(FewPropsSub09())
        list.add(FewPropsSub10())
        list.add(FewPropsSub11())
        list.add(FewPropsSub12())
        list.add(FewPropsSub13())
        list.add(FewPropsSub14())
        list.add(FewPropsSub15())
        list.add(FewPropsSub16())
        list.add(FewPropsSub17())
        list.add(FewPropsSub18())
        list.add(FewPropsSub19())
    }
}.also { list ->
    list.shuffled()
}.also { list ->
    list.modifyFewPropValues()
}

internal fun List<PropsToString>.modifyFewPropValues() {
    this.forEach { testObject ->
        testObject::class.memberProperties.forEach { prop ->
            when {
                prop.name =="propS" -> {
                    @Suppress("UNCHECKED_CAST")
                    prop as KMutableProperty1<PropsToString, String>
                    prop.setValue(testObject, prop, RandomStringUtils.random(5))
                }

                prop.name == "propI" -> {
                    @Suppress("UNCHECKED_CAST")
                    prop as KMutableProperty1<PropsToString, Int>
                    prop.setValue(testObject, prop, Random.nextInt())
                }

                prop.name == "propAny" -> {
                    @Suppress("UNCHECKED_CAST")
                    prop as KMutableProperty1<PropsToString, Any>
                    prop.setValue(
                        testObject,
                        prop,
                        Any()
                    )
                }

                prop.name == "propLs" -> {
                    @Suppress("UNCHECKED_CAST")
                    prop as KMutableProperty1<PropsToString, MutableList<String>>
                    prop.setValue(
                        testObject,
                        prop,
                        (0..250).map { RandomStringUtils.random(5) }.toMutableList()
                    )
                }

                prop.name == "propD" -> {
                    @Suppress("UNCHECKED_CAST")
                    prop as KMutableProperty1<PropsToString, Date>
                    prop.setValue(
                        testObject,
                        prop,
                        Date(Date().time + Random.nextLong(Short.MAX_VALUE.toLong()))
                    )
                }

                prop.name == "propAd" -> {
                    @Suppress("UNCHECKED_CAST")
                    prop as KMutableProperty1<PropsToString, Array<Double>>
                    prop.setValue(
                        testObject,
                        prop,
                        (0..300).map { Random.nextDouble() }.toTypedArray()
                    )
                }

                prop.name == "propAi" -> {
                    @Suppress("UNCHECKED_CAST")
                    prop as KMutableProperty1<PropsToString, IntArray>
                    prop.setValue(
                        testObject,
                        prop,
                        (0..500).map { Random.nextInt() }.toIntArray()
                    )
                }
            }
        }
    }
}

@State(Scope.Thread)
open class FewProps00: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub00: FewProps00() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()
            
    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps01: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub01: FewProps01() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps02: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub02: FewProps02() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps03: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub03: FewProps03() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps04: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub04: FewProps04() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps05: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub05: FewProps05() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps06: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub06: FewProps06() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps07: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub07: FewProps07() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps08: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub08: FewProps08() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps09: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub09: FewProps09() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps10: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub10: FewProps10() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps11: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub11: FewProps11() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps12: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub12: FewProps12() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps13: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub13: FewProps13() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps14: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub14: FewProps14() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps15: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub15: FewProps15() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps16: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub16: FewProps16() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps17: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub17: FewProps17() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps18: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub18: FewProps18() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}

@State(Scope.Thread)
open class FewProps19: PropsToString() {
    var propS: String = ""
    var propI: Int = 0
    var propAny: Any = Any()
    var propLs: MutableList<String> = ArrayList()
    var propD: Date = Date()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD)"
    }
}

@State(Scope.Thread)
open class FewPropsSub19: FewProps19() {

    var propAd: Array<Double> = emptyArray()
    var propAi: IntArray = intArrayOf()

    override fun withAsString(): String = this.asString()
    override fun withGson(): String = gson.toJson(this)
    override fun withToStringBuilder(): String = ToStringBuilder.reflectionToString(this)
    override fun withIdeGeneratedToString(): String {
        return "${this::class.simpleName}(propS='$propS', propI=$propI, propAny=$propAny, propLs=$propLs, propD=$propD, propAs=${propAd.contentToString()}, propAi=${propAi.contentToString()})"
    }
}


