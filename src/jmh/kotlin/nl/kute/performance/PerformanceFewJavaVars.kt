package nl.kute.performance

import nl.kute.asstring.annotation.additionalAnnotations
import nl.kute.asstring.annotation.modify.cachingRegexFactory
import nl.kute.asstring.annotation.option.asStringClassOptionCache
import nl.kute.asstring.core.objectCategoryCache
import nl.kute.asstring.core.useToStringByClass
import nl.kute.asstring.property.propsWithAnnotationsCacheByClass
import nl.kute.log.log
import nl.kute.performance.PerformanceFewJavaVars.Companion.callCountPerMethodPerIteration
import nl.kute.reflection.util.classToStringMethodCache
import nl.kute.testobjects.performance.PropsToString
import nl.kute.testobjects.performance.java.TestClassesFewJavaVars.propClassesFewJavaVars
import nl.kute.testobjects.performance.java.TestClassesFewJavaVars.propListFewJavaVarsAll
import nl.kute.testobjects.performance.java.TestClassesFewJavaVars.testObjectsFewJavaVars
import nl.kute.testobjects.performance.modifyFewPropValues
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.TearDown
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

// Set to `true` to enable the test
private var enabled = false

/**
 * Runs performance tests with **Kute** [nl.kute.asstring.core.asString], compared to
 * Apache's `ToStringBuilder`, `Gson.toJson()`, and IDE-generated toString.
 *  * The classes of this test have 7 properties each
 *  * **Kute** `asString` is used with vanilla options, so without any additional options specified.
 *
 *  * *Run gradle task *jmh* -> `jmh` to execute this test.*
 *  * Or run it using the generated jar: `build/libs/Kute-<version>-jmh.jar`
 *  * Alternatively, run it from your IDE if you can find a plugin that does the job
 *
 * The test typically runs in less than 15 minutes, depending on hardware & environment
 * > With [callCountPerMethodPerIteration] = 1000
 *  * If running it on a laptop, make sure it does not enter sleep mode.
 *     * on Mac you may run the command<br>
 *       `caffeinate -d -t 1800`
 *       <br>to keep it awake for half an hour (1800s)
 *     * for Linux and Windows, use Google :-) and/or consult documentation on how to prevent sleep mode
 *
 *  Total duration of the Gradle `jmh` task may take about 40 minutes if all tests are enabled.
 */
@State(Scope.Benchmark)
open class PerformanceFewJavaVars {

    @Setup(Level.Iteration)
    fun setUpIteration() {
        if (!enabled) {
            throw(IllegalStateException(disabledWarning))
        }
        testObjectsFewJavaVars.modifyFewPropValues()
        tasks.toMutableList().shuffled().forEach { task ->
            repeat(callCountPerMethodPerIteration) {
                charCount.addAndGet(task(testObjectsFewJavaVars[Random.nextInt(0, testObjectCount)]).length.toLong())
            }
        }
        log("""# of executions (total over iterations):
               | asString       : $asStringExecutionCount
               | ToStringBuilder: $toStringBuilderExecutionCount
               | gson:          : $gsonStringExecutionCount
               | IDE generated  : $ideToStringExceutionCount
        """.trimMargin())

    }

    @TearDown(Level.Trial)
    fun tearDownPerformanceTest() {
        if (!enabled) {
            throw(IllegalStateException(disabledWarning))
        }
        // charCount: just to make sure that the JVM does not eliminate the asString()/ToStringBuilder/gson/ideToString calls
        log("Iteration yielded $charCount characters")

        log(
            """Cache sizes:

            classToStringMethodCache        : ${classToStringMethodCache.size}
            propsWithAnnotationsCacheByClass: ${propsWithAnnotationsCacheByClass.size}
            objectCategoryCache             : ${objectCategoryCache.size}
            useToStringByClass              : ${useToStringByClass.size}
            asStringClassOptionCache        : ${asStringClassOptionCache.size}

            additionalAnnotations           : ${additionalAnnotations.size}
            cachingRegexFactory             : ${cachingRegexFactory.size}
        """.trimIndent()
        )
    }

    companion object {

        val callCountPerMethodPerIteration: Int = 1000

        val testObjectCount: Int = testObjectsFewJavaVars.size
        private val propertyCount: Int = propListFewJavaVarsAll.size
        val classCount: Int = propClassesFewJavaVars.size

        private val plan: PerformanceFewJavaVars = PerformanceFewJavaVars()

        val asStringExecutionCount: AtomicInteger = AtomicInteger(0)
        private val asStringTask: ToStringTask = { p ->
            asStringExecutionCount.incrementAndGet()
            plan.asString(p)
        }

        val toStringBuilderExecutionCount: AtomicInteger = AtomicInteger(0)
        private val toStringBuilderTask: ToStringTask = { p ->
            toStringBuilderExecutionCount.incrementAndGet()
            plan.toStringBuilder(p)
        }

        val ideToStringExceutionCount: AtomicInteger = AtomicInteger(0)
        private val ideToStringTask: ToStringTask = { p ->
            ideToStringExceutionCount.incrementAndGet()
            plan.ideGeneratedToString(p)
        }

        val gsonStringExecutionCount: AtomicInteger = AtomicInteger(0)
        private val gsonStringTask: ToStringTask = { p ->
            gsonStringExecutionCount.incrementAndGet()
            plan.gsonToJson(p)
        }

        val tasks: MutableSet<ToStringTask> =
            ConcurrentHashMap.newKeySet<ToStringTask?>()
                .also { it.addAll(listOf(asStringTask, toStringBuilderTask, ideToStringTask, gsonStringTask)) }

        var charCount: AtomicLong = AtomicLong(0)

        init {
            log("""

            This test measures the performance of **`asString()`** compared to:
             * Apache's `ToStringBuilder.reflectionToString()`
             * `gson.toJson()`
             * IDE generated toString()

            Each method is called $callCountPerMethodPerIteration times per iteration, in 4 threads.

            The test comprises $testObjectCount objects, with $propertyCount distinct `var` properties in $classCount classes.
            Between runs of the tests, the property values are updated.
            Each run of the test runs the various options (Kute, IDE-generated, Apache, Gson) in different (random) order.

            """.trimIndent())

            log("""

            The test typically runs in less than 15 minutes, depending on hardware & environment.
            If running it on a laptop, make sure it does not enter sleep mode.
             * on Mac you may run the command `caffeinate -d -t 1800` to keep it awake for half an hour (1800s)
             * for Linux and Windows, use Google :-) and/or consult documentation

        """.trimIndent())
        }
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.All)
    open fun asString(propsInstance: PropsToString): String = propsInstance.withAsString()

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.All)
    open fun toStringBuilder(propsInstance: PropsToString): String = propsInstance.withToStringBuilder()

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.All)
    open fun gsonToJson(propsInstance: PropsToString): String = propsInstance.withGson()

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.All)
    open fun ideGeneratedToString(propsInstance: PropsToString): String = propsInstance.withIdeGeneratedToString()
}