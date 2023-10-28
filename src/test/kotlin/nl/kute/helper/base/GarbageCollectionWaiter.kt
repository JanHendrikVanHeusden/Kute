package nl.kute.helper.base

import nl.kute.logging.log
import org.assertj.core.api.Assumptions.assumeThat
import org.awaitility.Awaitility
import org.awaitility.core.ConditionTimeoutException
import java.util.concurrent.TimeUnit

private typealias BooleanSupplier = () -> Boolean

interface GarbageCollectionWaiter {

    fun assertGarbageCollected(condition: BooleanSupplier, failOnTimeout: Boolean = false, maxWaitSeconds: Int = 2) {
        // If the tests do not succeed on your machine / JVM, you may be able to run this test successfully
        // by also using an external tool that enforces garbage collections, like VisualVM, JMeter or other monitoring tools.
        // (you may want to increase the waiting time)
        try {
            Awaitility.await()
                .alias("The referenced object should be weak referenced only, so eligible to garbage collection")
                .atMost(2L, TimeUnit.SECONDS)
                .with().pollInterval(100, TimeUnit.MILLISECONDS)
                .until {
                    System.gc()
                    condition.invoke()
                }
        } catch (e: ConditionTimeoutException) {
            if (failOnTimeout) {
                throw e
            } else {
                log(e.message)
                assumeThat(condition.invoke())
                    .`as`(explanationOnFailGcTest)
                    .isTrue
            }
        }
    }
}

private val explanationOnFailGcTest = """Some JVMs simply won't react on a call to System.gc().
                The test is executed, and succeeds on some JVMs; failure will be ignored though, as we can't be sure.
                Take care if it starts failing on a machine / JVM on which it succeeded previously.
                The test is also meant to document that the object (class) under test must not keep hard references to the object.
                """.replace(Regex("  +"), "\n")
