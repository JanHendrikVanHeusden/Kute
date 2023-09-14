package nl.kute.performance

import nl.kute.asstring.property.meta.PropertyMeta
import nl.kute.testobjects.performance.propsAllWithClass
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class MetaDataCachePerformance {

    val propertyCount = propsAllWithClass.size

    val cache: MutableMap<Pair<KProperty<*>, KClass<*>>, PropertyMeta> = ConcurrentHashMap()

    init {
        println("propertyCount: $propertyCount")
    }

    @Test
    fun test() {
        class Test {
            val test = ""
        }
        println(Test()::test == Test()::test)
        println(Test::test == Test::test)
    }
}