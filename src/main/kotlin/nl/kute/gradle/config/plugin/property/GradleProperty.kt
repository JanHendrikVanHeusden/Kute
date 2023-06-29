package nl.kute.gradle.config.plugin.property

import org.gradle.api.Project
import org.gradle.api.provider.Property
import kotlin.reflect.KProperty

internal class GradleProperty<T, V>(project: Project, type: Class<V>, default: V? = null) {

    val property: Property<V> = project.objects.property(type).apply {
        set(default)
    }

    operator fun getValue(thisRef: T, property: KProperty<*>): V =
        this.property.get()

    operator fun setValue(thisRef: T, property: KProperty<*>, value: V) =
        this.property.set(value)
}