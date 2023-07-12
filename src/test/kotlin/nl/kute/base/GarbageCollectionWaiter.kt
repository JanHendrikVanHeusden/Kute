package nl.kute.base

import nl.kute.core.namedvalues.Supplier
import nl.kute.log.log
import org.awaitility.Awaitility
import org.awaitility.core.ConditionTimeoutException
import java.util.concurrent.TimeUnit

internal class GarbageCollectionWaiter {

    companion object {
        fun waitUntilGarbageCollected(condition: Supplier<Boolean>, failOnTimeout: Boolean = false, maxWaitSeconds: Int = 2) {
            // assert
            try {
                Awaitility.await()
                    .alias("The referenced object should be weak referenced only, so eligible to garbage collection")
                    .atMost(maxWaitSeconds.toLong(), TimeUnit.SECONDS)
                    .with().pollInterval(200, TimeUnit.MILLISECONDS)
                    .until {
                        System.gc()
                        condition.invoke()
                    }
            } catch (e: ConditionTimeoutException) {
                if (failOnTimeout) {
                    throw e
                } else {
                    log(e.message)
                }
            }
        }

        val explanationOnFailGcTest = """Some JVMs simply won't react on a call to System.gc().
                The test is executed, and succeeds on some JVMs; failure will be ignored though, as we can't be sure.
                Take care if it starts failing on a machine / JVM on which it succeeded previously.
                
                The test is merely to document that the object (class) under test must not keep hard references to the object.
                You may be able to run this test successfully by also using an external tool that enforces garbage collections,
                like VisualVM, JMeter or other monitoring tools.""".replace(Regex("  +"), "\n")
    }
}