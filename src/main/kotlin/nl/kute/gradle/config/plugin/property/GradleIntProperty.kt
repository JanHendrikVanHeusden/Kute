package nl.kute.gradle.config.plugin.property

import org.gradle.api.Project
import org.gradle.api.provider.Property
import kotlin.reflect.KProperty

internal class GradleIntProperty<T>(project: Project, type: Class<Int>, default: Int? = null) {

    val property: Property<Int> = project.objects.property(type).apply {
        set(default)
    }

    operator fun getValue(thisRef: T, property: KProperty<*>): Int =
        this.property.get()

    operator fun setValue(thisRef: T, property: KProperty<*>, value: Int) =
        this.property.set(value)
}