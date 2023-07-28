package nl.kute.core.weakreference

import nl.kute.reflection.simplifyClassName
import nl.kute.test.base.GarbageCollectionWaiter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.Date
import kotlin.random.Random

internal class ObjectWeakReferenceTest : GarbageCollectionWaiter {

    val toStringResult = "the toString result"
    val hashCodeResult = Random(Date().time.toInt()).nextInt()

    private val testObject = object : Any() {
        override fun hashCode() = hashCodeResult
        override fun toString() = toStringResult
    }

    @Test
    fun `toString should output the toString result of the referenced object`() {
        val className = testObject::class.simplifyClassName()
        assertThat(ObjectWeakReference(testObject).toString())
            .isEqualTo("ObjectWeakReference of $className(${testObject})")
    }

    @Test
    fun `ObjectWeakReference should be null-safe`() {
        val withNull = ObjectWeakReference(null)
        assertThat(withNull.toString()).isNotEmpty
        assertThat(withNull.get() as Any?).isNull()
    }

    @Test
    fun `ObjectWeakReference should not prevent garbage collection of the referenced object`() {
        var toBeReferenced: Any? = Any()
        val weakRef = ObjectWeakReference(toBeReferenced)
        @Suppress("UNUSED_VALUE")
        toBeReferenced = null
        assertGarbageCollected({ weakRef.get() == null })
    }
}