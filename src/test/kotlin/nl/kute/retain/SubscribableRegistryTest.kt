package nl.kute.retain

import nl.kute.test.helper.isObjectAsString
import nl.kute.util.identityHashHex
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class SubscribableRegistryTest {

    // Todo: test subscriptions

    @Test
    fun `toString() should include the identity hash and the count of registered entries`() {
        // arrange
        val registry = SubscribableRegistry<String>()
        val idHash = registry.identityHashHex
        val valX = "x"
        val valY = "y"
        registry.register(valX)
        registry.register(valY)

        // act, assert
        Assertions.assertThat(registry.toString()).isObjectAsString(
            "SubscribableRegistry@$idHash",
            "#registered=2",
            "latestAddedId=2"
        )

        // arrange
        registry.clearAll()
        // act, assert
        Assertions.assertThat(registry.toString()).isObjectAsString(
            "SubscribableRegistry@$idHash",
            "#registered=0",
            "latestAddedId=0"
        )
        // just to ensure that the objects are not garbage collected before the #registered was evaluated
        Assertions.assertThat(registry).isNotNull
        Assertions.assertThat(valX).isNotNull()
        Assertions.assertThat(valY).isNotNull()
    }

}