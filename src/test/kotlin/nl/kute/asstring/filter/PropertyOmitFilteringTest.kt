package nl.kute.asstring.filter

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import nl.kute.asstring.config.asStringConfig
import nl.kute.asstring.core.asString
import nl.kute.asstring.core.filter.PropertyMetaFilter
import nl.kute.asstring.core.propertyOmitFilterRegistry
import nl.kute.exception.throwableAsString
import nl.kute.helper.annotations.Entity
import nl.kute.helper.annotations.Id
import nl.kute.helper.annotations.ManyToOne
import nl.kute.helper.annotations.OneToMany
import nl.kute.helper.helper.isObjectAsString
import nl.kute.logging.logger
import nl.kute.logging.resetStdOutLogger
import nl.kute.reflection.util.simplifyClassName
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.jvm.javaField
import kotlin.reflect.typeOf

class PropertyOmitFilteringTest {

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        propertyOmitFilterRegistry.clearAll()
        resetStdOutLogger()
    }

// region ~ Tests

    @Test
    fun `properties should be filtered out according to single filter`() {
        // arrange
        @Suppress("unused")
        class MyClassForPropertyFiltering {
            val shouldBeThere = "I should be there"
            val filterMeOut = "I should be filtered out"
        }
        // without filter
        assertThat(asStringConfig().getPropertyOmitFilters()).isEmpty()
        assertThat(MyClassForPropertyFiltering().asString())
            .isObjectAsString("MyClassForPropertyFiltering",
                "shouldBeThere=I should be there",
                "filterMeOut=I should be filtered out"
            )

        // act: apply filter
        val filter: PropertyMetaFilter = { meta -> meta.propertyName == "filterMeOut" }
        val filterId = propertyOmitFilterRegistry.register(filter)

        // assert
        assertThat(MyClassForPropertyFiltering().asString())
            .isObjectAsString("MyClassForPropertyFiltering",
                "shouldBeThere=I should be there"
            )
        assertThat(asStringConfig().getPropertyOmitFilters())
            .containsExactly(entry(filter, filterId))
            .hasSize(1)
    }

    @Test
    fun `properties should be filtered by collection-like when specified`() {
        // arrange
        assertThat(asStringConfig().getPropertyOmitFilters()).isEmpty()

        @Suppress("unused")
        class TestPropFilter {
            val listToExclude = listOf("I will be excluded")
            val arrayToExclude = arrayOf("I will be excluded too")
            val mapToExclude = mapOf("I" to "will also be excluded")
            lateinit var lateInitToExclude: Collection<*>
            val included = "I will be there"

            override fun toString() = this.asString()
        }

        // act, assert
        assertThat(TestPropFilter().toString())
            .isObjectAsString("TestPropFilter",
                "listToExclude=[I will be excluded]",
                "arrayToExclude=[I will be excluded too]",
                "mapToExclude={I=will also be excluded}",
                "included=I will be there",
                "lateInitToExclude=null"
            )

        // arrange
        val filter: PropertyMetaFilter = { m -> m.isCollectionLike }
        asStringConfig().withPropertyOmitFilters(filter).applyAsDefault()

        // act, assert
        assertThat(TestPropFilter().toString())
            .isObjectAsString("TestPropFilter",
                "included=I will be there"
            )
    }

    /**
     * This is more or less a **demo** how collections JPA / Hibernate child-entities can be omitted.
     * Note that the @[Entity], @[OneToMany] etc. annotations are local annotation classes,
     * not the real JPA or Hibernate stuff.
     * But anyway, meant to give an idea how to filter these in a real-world situation.
     */
    @Test
    fun `omit properties with field annotated OneToMany`() {

        // arrange
        val part1  = Part(UUID.randomUUID(), "part1", mutableListOf())
        val part2  = Part(UUID.randomUUID(), "part2", mutableListOf())
        val product = Product(UUID.randomUUID(), "the product", listOf(part1, part2))

        // Without filtering child entities, your application might run into **performance issues** !!
        //   OneToMany relationships often are fetched lazily;
        //   but reflective access of getter would probably trigger the database fetch !!
        //
        // Also, without filter, output of asString() looks quite messy, due to mutual references,
        // something like this:
        //   Product(id=ce479863-9763-4de5-b1c6-196be2ecca12, name=the product, parts=[Part(id=34a621e5-cc74-4f7f-b5e3-d6a4c95b74d8, includedIn=[recursive: Product(...)], name=part1), Part(id=9b8bab16-20b8-4762-9df6-ac973a2c0379, includedIn=[recursive: Product(...)], name=part2)])
        //   Part(id=34a621e5-cc74-4f7f-b5e3-d6a4c95b74d8, includedIn=[Product(id=ce479863-9763-4de5-b1c6-196be2ecca12, name=the product, parts=[recursive: Part(...), Part(id=9b8bab16-20b8-4762-9df6-ac973a2c0379, includedIn=[recursive: Product(...)], name=part2)])], name=part1)
        //   Part(id=9b8bab16-20b8-4762-9df6-ac973a2c0379, includedIn=[Product(id=ce479863-9763-4de5-b1c6-196be2ecca12, name=the product, parts=[Part(id=34a621e5-cc74-4f7f-b5e3-d6a4c95b74d8, includedIn=[recursive: Product(...)], name=part1), recursive: Part(...)])], name=part2)

        assertThat(product.asString())
            .`as`("No filter applied yet, so asString() of product should include the parts" +
                        " (each part with recursive back-reference to Product)"
            ).contains(
                "Product",
                "id=${product.id}",
                "name=${product.name}",
                "parts=[",
                "name=part1",
                "name=part2",
                "includedIn=[recursive: Product(...)"
            )

        assertThat(part1.asString())
            .`as`("No filter applied yet, so asString() of part1 should include references to Product," +
                    " also recursive back references to parts")
            .contains(
                "Part",
                "id=${part1.id}",
                "includedIn=[Product(id=${product.id}, name=${product.name}, parts=[recursive: Part(...), Part(id=${part2.id}",
                "includedIn=[recursive: Product(...)]",
                "name=part1",
                "recursive: Part(...)",
                "name=part2"
            )

        // create and apply the filter
        val entityFilterNoChildren: PropertyMetaFilter = { m ->
            m.objectClass.hasAnnotation<Entity>() &&
            m.isCollectionLike &&
            m.property.javaField?.isAnnotationPresent(OneToMany::class.java) == true
        }
        asStringConfig().withPropertyOmitFilters(entityFilterNoChildren).applyAsDefault()

        // act, assert

        // With filter, the output looks a lot cleaner, something like:
        //   Product(id=ce479863-9763-4de5-b1c6-196be2ecca12, name=the product)
        //   Part(id=34a621e5-cc74-4f7f-b5e3-d6a4c95b74d8, includedIn=[Product(id=ce479863-9763-4de5-b1c6-196be2ecca12, name=the product)], name=part1)
        //   Part(id=9b8bab16-20b8-4762-9df6-ac973a2c0379, includedIn=[Product(id=ce479863-9763-4de5-b1c6-196be2ecca12, name=the product)], name=part2)
        // Note that (deliberately) only the OneToMany is filtered out. The ManyToOne is not filtered out.
        // Of course, decide yourself what is desirable, depending on your actual object model etc.

        assertThat(product.asString())
            .`as`("Filter applied, so asString() of product should not include parts anymore")
            .isObjectAsString(
                "Product",
                "id=${product.id}",
                "name=${product.name}"
            ).doesNotContain("parts=")

        assertThat(part1.asString())
            .`as`("Filter on Product.parts removes back-reference to Parts, so no recursive entries anymore")
            .isObjectAsString(
                "Part",
                "id=${part1.id}",
                "includedIn=[${product.asString()}]",
                "name=${part1.name}"
            ).doesNotContain("recursive: ", part2.id.toString(), part2.name)

        val subProduct = SubProduct(UUID.randomUUID(), "the product", listOf(part1, part2))
        assertThat(subProduct.asString())
            .isObjectAsString(
                "SubProduct",
                "id=${subProduct.id}",
                "name=${subProduct.name}"
            ).doesNotContain("parts=")
    }

    /**
     * This is more or less a **demo** how collections JPA / Hibernate child-entities can be omitted.
     * Note that the @[Entity], @[OneToMany] etc. annotations are local annotation classes,
     * not the real JPA or Hibernate stuff.
     * But anyway, meant to give an idea how to filter these in a real-world situation.
     */
    @Test
    fun `omit properties with getter annotated OneToMany`() {

        // arrange
        val part1  = Part(UUID.randomUUID(), "part1", mutableListOf())
        val part2  = Part(UUID.randomUUID(), "part2", mutableListOf())
        val product = Product(UUID.randomUUID(), "the product", listOf(part1, part2))

        // Without filtering child entities, your application might run into **performance issues** !!
        //   OneToMany relationships typically are fetched lazily;
        //   but reflective access of getter would probably trigger the database fetch !!
        //
        // Also, without filter, output of asString() looks quite messy, due to mutual references,
        // something like this:
        //   Product(id=ce479863-9763-4de5-b1c6-196be2ecca12, name=the product, parts=[Part(id=34a621e5-cc74-4f7f-b5e3-d6a4c95b74d8, includedIn=[recursive: Product(...)], name=part1), Part(id=9b8bab16-20b8-4762-9df6-ac973a2c0379, includedIn=[recursive: Product(...)], name=part2)])
        //   Part(id=34a621e5-cc74-4f7f-b5e3-d6a4c95b74d8, includedIn=[Product(id=ce479863-9763-4de5-b1c6-196be2ecca12, name=the product, parts=[recursive: Part(...), Part(id=9b8bab16-20b8-4762-9df6-ac973a2c0379, includedIn=[recursive: Product(...)], name=part2)])], name=part1)
        //   Part(id=9b8bab16-20b8-4762-9df6-ac973a2c0379, includedIn=[Product(id=ce479863-9763-4de5-b1c6-196be2ecca12, name=the product, parts=[Part(id=34a621e5-cc74-4f7f-b5e3-d6a4c95b74d8, includedIn=[recursive: Product(...)], name=part1), recursive: Part(...)])], name=part2)

        assertThat(product.asString())
            .`as`("No filter applied yet, so asString() of product should include the parts" +
                    " (each part with recursive back-reference to Product)"
            ).contains(
                "Product",
                "id=${product.id}",
                "name=${product.name}",
                "parts=[",
                "name=part1",
                "name=part2",
                "includedIn=[recursive: Product(...)"
            )

        assertThat(part1.asString())
            .`as`("No filter applied yet, so asString() of part1 should include references to Product," +
                    " also recursive back references to parts")
            .contains(
                "Part",
                "id=${part1.id}",
                "includedIn=[Product(id=${product.id}, name=${product.name}, parts=[recursive: Part(...), Part(id=${part2.id}",
                "includedIn=[recursive: Product(...)]",
                "name=part1",
                "recursive: Part(...)",
                "name=part2"
            )

        // create and apply the filter
        val entityFilterNoChildren: PropertyMetaFilter = { m ->
            m.objectClass.hasAnnotation<Entity>() &&
                    m.isCollectionLike &&
                    m.property.getter.hasAnnotation<OneToMany>()
        }
        asStringConfig().withPropertyOmitFilters(entityFilterNoChildren).applyAsDefault()

        // act, assert

        // With filter, the output looks a lot cleaner, something like:
        //   Product(id=ce479863-9763-4de5-b1c6-196be2ecca12, name=the product)
        //   Part(id=34a621e5-cc74-4f7f-b5e3-d6a4c95b74d8, includedIn=[Product(id=ce479863-9763-4de5-b1c6-196be2ecca12, name=the product)], name=part1)
        //   Part(id=9b8bab16-20b8-4762-9df6-ac973a2c0379, includedIn=[Product(id=ce479863-9763-4de5-b1c6-196be2ecca12, name=the product)], name=part2)
        // Note that (deliberately) only the OneToMany is filtered out. The ManyToOne is not filtered out.
        // Of course, decide yourself what is desirable, depending on your actual object model etc.

        assertThat(product.asString())
            .`as`("Filter applied, so asString() of product should not include parts anymore")
            .isObjectAsString(
                "Product",
                "id=${product.id}",
                "name=${product.name}"
            ).doesNotContain("parts=")

        assertThat(part1.asString())
            .`as`("Filter on Product.parts removes back-reference to Parts, so no recursive entries anymore")
            .isObjectAsString(
                "Part",
                "id=${part1.id}",
                "includedIn=[${product.asString()}]",
                "name=${part1.name}"
            ).doesNotContain("recursive: ", part2.id.toString(), part2.name)

        val subProduct = SubProduct(UUID.randomUUID(), "the product", listOf(part1, part2))
        assertThat(subProduct.asString())
            .isObjectAsString(
                "SubProduct",
                "id=${subProduct.id}",
                "name=${subProduct.name}"
            ).doesNotContain("parts=")
    }

    @Test
    fun `properties should be filtered out according to multiple filters`() {
        // arrange
        @Suppress("unused")
        open class MyClassForPropertyFiltering {
            val shouldBeThere = "I should be there"
            open val filterMeOut = "I should be filtered out"
            private val listValToFilterOut: List<Any> = emptyList()
        }
        @Suppress("unused")
        open class MySubClassForPropertyFiltering: MyClassForPropertyFiltering() {
            override val filterMeOut = "I should not be filtered out"
            private val anotherListValToFilterOut: Collection<Any> = emptyList()
        }

        // without filters
        assertThat(asStringConfig().getPropertyOmitFilters()).isEmpty()

        // act, assert: no filters
        assertThat(MyClassForPropertyFiltering().asString())
            .isObjectAsString("MyClassForPropertyFiltering",
                "shouldBeThere=I should be there",
                "filterMeOut=I should be filtered out",
                "listValToFilterOut=[]"
            )
        assertThat(MySubClassForPropertyFiltering().asString())
            .isObjectAsString("MySubClassForPropertyFiltering",
                "shouldBeThere=I should be there",
                "filterMeOut=I should not be filtered out",
                "anotherListValToFilterOut=[]"
            )

        // arrange: filters
        val filter1: PropertyMetaFilter = { meta ->
            meta.objectClass == MyClassForPropertyFiltering::class
                    && meta.propertyName == "filterMeOut"
        }
        val filter2: PropertyMetaFilter = { meta ->
            meta.returnType.classifier == List::class
        }
        val filter3: PropertyMetaFilter = { meta ->
            meta.returnType.isSubtypeOf(typeOf<Collection<*>>())
        }
        // applying filters 1 & 2
        asStringConfig().withPropertyOmitFilters(filter1, filter2).applyAsDefault()

        assertThat(asStringConfig().getPropertyOmitFilters().keys)
            .containsExactlyInAnyOrder(filter1, filter2)
            .hasSize(2)

        // act, assert
        assertThat(MyClassForPropertyFiltering().asString())
            .isObjectAsString("MyClassForPropertyFiltering",
                "shouldBeThere=I should be there",
            )
        assertThat(MySubClassForPropertyFiltering().asString())
            .isObjectAsString("MySubClassForPropertyFiltering",
                "shouldBeThere=I should be there",
                "filterMeOut=I should not be filtered out",
                "anotherListValToFilterOut=[]"
            )

        // applying filters 1, 2, 3
        asStringConfig().withPropertyOmitFilters(filter1, filter2, filter3).applyAsDefault()

        assertThat(asStringConfig().getPropertyOmitFilters().keys)
            .containsExactlyInAnyOrder(filter1, filter2, filter3)
            .hasSize(3)

        // act, assert
        assertThat(MySubClassForPropertyFiltering().asString())
            .isObjectAsString("MySubClassForPropertyFiltering",
                "shouldBeThere=I should be there",
                "filterMeOut=I should not be filtered out",
            )

        // arrange: reset filters
        asStringConfig().withPropertyOmitFilters().applyAsDefault()

        // act, assert: no filters
        assertThat(MyClassForPropertyFiltering().asString())
            .isObjectAsString("MyClassForPropertyFiltering",
                "shouldBeThere=I should be there",
                "filterMeOut=I should be filtered out",
                "listValToFilterOut=[]"
            )
        assertThat(MySubClassForPropertyFiltering().asString())
            .isObjectAsString("MySubClassForPropertyFiltering",
                "shouldBeThere=I should be there",
                "filterMeOut=I should not be filtered out",
                "anotherListValToFilterOut=[]"
            )
    }

    @Test
    fun `private nested  classes should yield a decent result`() {
        // arrange
        assertThat(PrivateLocalTestClass().asString())
            .`as`("propA should not be filtered out yet, no filter applied yet")
            .isObjectAsString(
                "PrivateLocalTestClass",
                 "propA=prop A",
                "propB=prop B"
            )

        val filter: PropertyMetaFilter =
            // this won't compile, as propA is private:
            // { meta -> meta.propertyName == PrivateLocalTestClass::propA.name }

            // same filter as comment above, but now from companion object - does it work?
            PrivateLocalTestClass.filter

        asStringConfig().withPropertyOmitFilters(filter).applyAsDefault()

        // act, assert: it works :-)
        assertThat(PrivateLocalTestClass().asString())
            .`as`("propA should be filtered out, the filter should handle it properly even if the property is private")
            .isObjectAsString(
            "PrivateLocalTestClass",
            // "propA=prop A",
            "propB=prop B"
        )
    }

    @Test
    fun `filtering out by property return-type`() {
        @Suppress("unused")
        open class ClassWithGsonProp {
            val gson: Gson = GsonBuilder().create() // omitting builder options
            val gsonBuilder: GsonBuilder = GsonBuilder()
            val anotherProp: String = "an other property"
        }
        assertThat(ClassWithGsonProp().asString())
            .`as`("Lots of Gson internal stuff here...")
            .hasSizeGreaterThan(1000)

        val gsonFilter: PropertyMetaFilter =
            { m -> m.returnType.classifier == Gson::class || m.returnType.classifier == GsonBuilder::class}
        asStringConfig().withPropertyOmitFilters(gsonFilter).applyAsDefault()

        assertThat(ClassWithGsonProp().asString())
            .`as`("Gson stuff should be filtered out")
            .isEqualTo("ClassWithGsonProp(anotherProp=an other property)")
    }

    @Test
    fun `filtering out by property's return-type package name`() {
        @Suppress("unused")
        open class ClassWithGsonProp {
            val gson: Gson = Gson()
            val gsonBuilder: GsonBuilder = GsonBuilder()
            val anotherProp: String = "an other property"
        }
        assertThat(ClassWithGsonProp().asString())
            .`as`("Lots of Gson internal stuff here...")
            .hasSizeGreaterThan(1000)

        val gsonPackageName = Gson::class.java.packageName
        val gsonFilter: PropertyMetaFilter =
            { m -> m.returnType.classifier.let { it is KClass<*> && it.java.packageName.startsWith(gsonPackageName) }}
        asStringConfig().withPropertyOmitFilters(gsonFilter).applyAsDefault()

        assertThat(ClassWithGsonProp().asString())
            .`as`("Gson stuff should be filtered out")
            .isEqualTo("ClassWithGsonProp(anotherProp=an other property)")
    }

    @Test
    fun `filters that throw an Exception should be removed and have their Exception logged`() {
        // arrange
        val errorMsg = "I crashed!"
        val exception = IllegalStateException(errorMsg)
        val throwingFilter: PropertyMetaFilter = { _ -> throw exception }
        val dummyFilter: PropertyMetaFilter = { _ -> false }
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }

        @Suppress("unused")
        class TestClass {
            val prop1 = "prop 1"
            val prop2 = "prop 2"
        }

        asStringConfig().withPropertyOmitFilters(throwingFilter, dummyFilter).applyAsDefault()

        // act, assert
        assertThat(propertyOmitFilterRegistry.entries()).containsExactlyInAnyOrder(throwingFilter, dummyFilter)
        assertThat(TestClass().asString()).isObjectAsString(
            "TestClass",
            "prop1=prop 1",
            "prop2=prop 2"
        )
        assertThat(logMsg).contains(
            IllegalStateException::class.simplifyClassName(),
            errorMsg,
            exception.throwableAsString(),
            "PropertyOmitFilter threw IllegalStateException",
            "this particular filter will be removed from the registry"
        )
        assertThat(propertyOmitFilterRegistry.entries())
            // throwingFilter should be removed when exception encountered
            .containsExactly(dummyFilter)

        logMsg = ""
        assertThat(TestClass().asString()).isObjectAsString(
            "TestClass",
            "prop1=prop 1",
            "prop2=prop 2"
        )
        assertThat(logMsg)
            .`as`("No exception should be logged anymore because throwing filter has been removed")
            .isEmpty()
    }

// endregion

// region ~ Classes, objects, helpers  etc. to be used for testing

    @Suppress("unused")
    private open class PrivateLocalTestClass {
        private val propA = "prop A"
        protected val propB = "prop B"

        companion object {
            val filter: PropertyMetaFilter = { meta ->
                meta.propertyName == PrivateLocalTestClass::propA.name
            }
        }
    }

// endregion

}

@Entity
class Part(@Id val id: UUID, val name: String, @ManyToOne val includedIn: MutableList<Product>)

/** The @[OneToMany] annotation needs the site-target to make the filtering work */
@Entity
open class Product(@Id val id: UUID, val name: String, @field:OneToMany @get:OneToMany open val parts: List<Part>) {
    init {
        if (this::class == Product::class) {
            @Suppress("LeakingThis")
            this.parts.forEach { it.includedIn.add(this) }
        }
    }
}

@Entity
open class SubProduct(id: UUID, name: String, subProductParts: List<Part>): Product(id, name, subProductParts) {
    @OneToMany // implicitly applies to field
    @get:OneToMany
    override val parts: List<Part> = subProductParts
}
