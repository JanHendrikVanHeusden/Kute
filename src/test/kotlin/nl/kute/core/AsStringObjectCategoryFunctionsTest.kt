@file:Suppress("unused")

package nl.kute.core

import nl.kute.config.AsStringConfig
import nl.kute.config.restoreInitialAsStringClassOption
import nl.kute.core.annotation.option.AsStringClassOption
import nl.kute.core.annotation.option.AsStringOption
import nl.kute.core.test.helper.isObjectAsString
import nl.kute.reflection.hasImplementedToString
import nl.kute.util.identityHashHex
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Stack
import java.util.UUID

class AsStringObjectCategoryFunctionsTest {

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        restoreInitialAsStringClassOption()
    }

    @Test
    fun `asStringFallBack should include the same identity as non-overridden toString`() {
        val identityRegex = Regex("""^.+(@[0-9a-f]+)$""")
        @Suppress("LocalVariableName")
        val `@hexHash` = "\$1"
        // Non-overridden toString() removes leading zeroes from the hex hashCode
        // So the test is repeated a number of times, so we are reasonably sure that it would hit a leading zero
        // (which asStringFallBack also needs to remove)
        (1..100).forEach { _ ->
            Any().let {
                assertThat(it.asStringFallBack().replace(identityRegex, `@hexHash`))
                    .isNotEmpty
                    .doesNotStartWith("0")
                    .isEqualTo(it.toString().replace(identityRegex, `@hexHash`))
            }
        }
    }

    @Test
    fun `asStringFallBack should handle nulls correctly`() {
        assertThat(null.asStringFallBack()).isEqualTo("null")
    }

    @Test
    fun `objectIdentity should honour AsStringClassOption`() {
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        @AsStringClassOption(includeIdentityHash = true)
        class WithAnnotation
        var testObj: Any = WithAnnotation()
        var idHash = testObj.identityHashHex
        assertThat(testObj.objectIdentity()).isEqualTo("WithAnnotation@$idHash")

        class WithoutAnnotation
        testObj = WithoutAnnotation()
        assertThat(testObj.objectIdentity()).isEqualTo("WithoutAnnotation")

        AsStringConfig().withIncludeIdentityHash(includeHash = true).applyAsDefault()
        idHash = testObj.identityHashHex
        assertThat(testObj.objectIdentity()).isEqualTo("WithoutAnnotation@$idHash")
    }

    @Test
    fun `collectionIdentity should honour default AsStringClassOption`() {
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        val collections = listOf(arrayOf(12.0), listOf<Any>(), mapOf(1 to "one", 2 to "two"), Stack<String>())
        collections.forEach {
            restoreInitialAsStringClassOption()
            val collection = it
            val className = collection::class.simpleName
            val idHash = collection.identityHashHex

            assertThat(collection.collectionIdentity())
                .`as`("When no identity required, it follows the default toString() of the Collection, so no className")
                .isEqualTo("")

            assertThat(collection.collectionIdentity(includeIdentity = true))
                .isEqualTo("$className@$idHash")

            AsStringConfig().withIncludeIdentityHash(includeHash = true).applyAsDefault()
            assertThat(collection.collectionIdentity())
                .isEqualTo("$className@$idHash")
        }
    }

    @Test
    fun `collectionAsString should honour default AsStringClassOption`() {
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        val collection: Collection<String> = Stack<String>().also { it.addAll(listOf("one", "two")) }
        val className = collection::class.simpleName
        val idHash = collection.identityHashHex

        assertThat(collection.collectionAsString())
            .isEqualTo("[one, two]")

        AsStringConfig().withIncludeIdentityHash(includeHash = true).applyAsDefault()
        assertThat(collection.collectionAsString())
            .isEqualTo("$className@$idHash[one, two]")
    }

    @Test
    fun `arrayAsString should honour default AsStringClassOption`() {
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        val array: Array<String> = arrayOf("one", "two")
        val className = array::class.simpleName
        val idHash = array.identityHashHex

        assertThat(array.arrayAsString())
            .isEqualTo("[one, two]")

        AsStringConfig().withIncludeIdentityHash(includeHash = true).applyAsDefault()
        assertThat(array.arrayAsString())
            .isEqualTo("$className@$idHash[one, two]")
    }

    @Test
    fun `mapAsString should honour default AsStringClassOption`() {
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        val map = mapOf(1 to "one", 2 to "two")
        val className = map::class.simpleName
        val idHash = map.identityHashHex

        assertThat(map.mapAsString())
            .isEqualTo("{1=one, 2=two}")

        AsStringConfig().withIncludeIdentityHash(includeHash = true).applyAsDefault()
        assertThat(map.mapAsString())
            .isEqualTo("$className@$idHash{1=one, 2=two}")
    }

    @Test
    fun `systemClassIdentity should honour default AsStringClassOption`() {
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        // arbitrary list of some Java and Kotlin built-in stuff
        val systemStuff = listOf(System.out, UUID.randomUUID(), '0', arrayListOf('1'), StringBuffer("x"))
        systemStuff.forEach {
            restoreInitialAsStringClassOption()
            val systemObj = it
            val className = systemObj::class.simpleName
            val idHash = systemObj.identityHashHex

            assertThat(systemObj.systemClassIdentity())
                .isEqualTo(className)

            assertThat(systemObj.systemClassIdentity(includeIdentity = true))
                .isEqualTo("$className@$idHash")

            AsStringConfig().withIncludeIdentityHash(includeHash = true).applyAsDefault()
            assertThat(systemObj.systemClassIdentity())
                .isEqualTo("$className@$idHash")
        }
    }

    @Test
    fun `systemClassObjAsString should honour default AsStringClassOption`() {
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        // arbitrary list of some Java and Kotlin built-in stuff
        val systemStuff = listOf(System.out, UUID.randomUUID(), arrayListOf('1'), StringBuffer("x"))
        systemStuff.forEach {
            restoreInitialAsStringClassOption()
            val systemObj = it
            val className = systemObj::class.simpleName
            val idHash = systemObj.identityHashHex

            if (systemObj::class.hasImplementedToString()) {
                assertThat(systemObj.systemClassObjAsString())
                    .isEqualTo(systemObj.toString())
            } else {
                assertThat(systemObj.systemClassObjAsString())
                    .isEqualTo("$className")
            }

            AsStringConfig().withIncludeIdentityHash(includeHash = true).applyAsDefault()
            if (systemObj::class.hasImplementedToString()) {
                assertThat(systemObj.systemClassObjAsString())
                    .isEqualTo(systemObj.toString())
            } else {
                assertThat(systemObj.systemClassObjAsString())
                    .isEqualTo("$className@$idHash")
            }
        }
    }

    @Test
    fun `annotationAsString should remove the package name`() {
        val defaultOption = AsStringOption.defaultOption
        assertThat(defaultOption.annotationAsString())
            .isObjectAsString("AsStringOption",
                "showNullAs=${defaultOption.showNullAs}",
                "surroundPropValue=NONE",
                "propMaxStringValueLength=${defaultOption.propMaxStringValueLength}",
                "elementsLimit=${defaultOption.elementsLimit}"
            )
    }

    @Test
    fun `syntheticClassObjectAsString should return the lambda type and honour default AsStringClassOption`() {
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse

        val stringSupplier = { "this is the lambda return value" }
        val intFunction: (Int, Int) -> Int = { i, j -> i + j }
        val swap: (Pair<Int, Int>) -> Pair<Int, Int> = { p -> Pair(p.second, p.first) }

        assertThat(stringSupplier.syntheticClassObjectAsString())
            .isEqualTo("() -> String")
        assertThat(intFunction.syntheticClassObjectAsString())
            .isEqualTo("(Int, Int) -> Int")
        assertThat(swap.syntheticClassObjectAsString())
            .isEqualTo("(Pair<Int, Int>) -> Pair<Int, Int>")

        AsStringConfig().withIncludeIdentityHash(true).applyAsDefault()

        assertThat(stringSupplier.syntheticClassObjectAsString())
            .`as`("no identity hash should be included in lambda representation")
            .isEqualTo("() -> String")
    }

    @Test
    fun `syntheticClassObjectAsString should return asStringFallBack for non-lambdas`() {
        assertThat(System.out.syntheticClassObjectAsString() )
            .isEqualTo(System.out.asStringFallBack())

        class Dummy {
            var dummy: Int = 0
            fun setTheDummy(x: Int) {
                this.dummy = x
            }
        }
        val unit: Unit = Dummy().setTheDummy(1)
        assertThat(unit.syntheticClassObjectAsString())
            .isEqualTo(unit.asStringFallBack())
    }

}