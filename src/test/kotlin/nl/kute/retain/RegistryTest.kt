package nl.kute.retain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.Test

private typealias SomeLambda = (Any) -> Boolean

class RegistryTest {

    @Test
    fun `test basic functions of Registry`() {

        val registry: Registry<SomeLambda> = Registry()

        val obj1: SomeLambda = { _ -> true }
        val obj2: SomeLambda = { _ -> true } // identical, but not same object

        // empty yet
        assertThat(registry.hasEntry()).isFalse
        assertThat(registry.entries()).hasSize(0)

        // register
        val id1: Int = registry.register(obj1)
        val id2: Int = registry.register(obj2)

        // assert added entries
        assertThat(registry.entries())
            .containsExactlyInAnyOrder(obj1, obj2)
            .hasSize(2)

        assertThat(registry.getEntryMap().entries)
            .contains(entry(obj1, id1), entry(obj2, id2))

        // replace obj2 by (already present) obj1, should leave obj1 only in the result
        registry.replaceAll(obj1, obj1)
        assertThat(registry.entries())
            .containsExactly(obj1)
            .hasSize(1)

        // re-register obj1, still obj1 only
        val id1SecondTime = registry.register(obj1)
        assertThat(id1SecondTime).isEqualTo(id1)

        assertThat(registry.entries())
            .containsExactly(obj1)
            .hasSize(1)

        // re-register obj2, should be added now
        registry.register(obj2)
        assertThat(registry.entries())
            .containsExactlyInAnyOrder(obj1, obj2)
            .hasSize(2)

        val clearedEntryIds: Collection<SomeLambda> = registry.clearAll()
        assertThat(clearedEntryIds).containsExactlyInAnyOrder(obj1, obj2)

        val id2SecondTime = registry.register(obj2)
        val removed2: SomeLambda? = registry.remove(id2SecondTime)
        assertThat(removed2).isSameAs(obj2)
    }

}