package nl.kute.performance

import nl.kute.asstring.property.meta.PropertyMeta
import nl.kute.testobjects.performance.propsAllWithClass
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class MetaDataCachePerformance {

    val propertyCount = propsAllWithClass.size.also { println("propertyCount: $it") }

    val cache: MutableMap<Pair<KProperty<*>, KClass<*>>, PropertyMeta> = ConcurrentHashMap()
}