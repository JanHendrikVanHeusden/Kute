package nl.kute.performance

import nl.kute.asstring.annotation.additionalAnnotations
import nl.kute.asstring.annotation.modify.cachingRegexFactory
import nl.kute.asstring.annotation.option.asStringClassOptionCache
import nl.kute.asstring.core.objectCategoryCache
import nl.kute.asstring.core.useToStringByClass
import nl.kute.asstring.property.propsWithAnnotationsCacheByClass
import nl.kute.log.log
import nl.kute.performance.PerformanceSingleShot.Companion.callCountPerMethodPerIteration
import nl.kute.reflection.util.classToStringMethodCache
import nl.kute.testobjects.performance.PropsToString
import nl.kute.testobjects.performance.modifyManyClassesPropValues
import nl.kute.testobjects.performance.testObjectsManyClasses
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.TearDown
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

// Set to `true` to enable the test
private var enabled = true

/**
 * Runs performance tests with **Kute** [nl.kute.asstring.core.asString], compared to
 * Apache's `ToStringBuilder`, `Gson.toJson()`, and IDE-generated toString.
 *  * The 1000 classes of this test have 5 properties each, but each class is different from each other class.
 *  * For each class, only a single test-object is instantiated.
 *  * The classes are randomly selected, but on average each object is touched only once.
 *   So [nl.kute.asstring.core.asString] does not benefit of caching of class info (properties, annotations, etc.).
 *  * **Kute** `asString` is used with vanilla options, so without any additional options specified.
 *
 *  * *Run gradle task *jmh* -> `jmh` to execute this test.*
 *  * Or run it using the generated jar: `build/libs/Kute-<version>-jmh.jar`
 *  * Alternatively, run it from your IDE if you can find a plugin that does the job
 *
 * The test typically runs in less than 15 minutes, depending on hardware & environment
 * > With [callCountPerMethodPerIteration] = 100
 *  * If running it on a laptop, make sure it does not enter sleep mode.
 *     * on Mac you may run the command<br>
 *       `caffeinate -d -t 1800`
 *       <br>to keep it awake for half an hour (1800s)
 *     * for Linux and Windows, use Google :-) and/or consult documentation on how to prevent sleep mode
 *
 *  Total duration of the Gradle `jmh` task may take about 40 minutes if all tests are enabled.
 */
@State(Scope.Benchmark)
@Fork(value = 1, warmups = 0)
@BenchmarkMode(Mode.SingleShotTime)
@Warmup(iterations = 1, batchSize = 1, time = 1, timeUnit = TimeUnit.NANOSECONDS)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
open class PerformanceSingleShot {

    @Setup(Level.Iteration)
    fun setUpIteration() {
        if (!enabled) {
            throw(IllegalStateException(disabledWarning))
        }
        testObjectsManyClasses.modifyManyClassesPropValues()
        tasks.toMutableList().shuffled().forEach { task ->
            repeat(callCountPerMethodPerIteration) {
                charCount.addAndGet(task(testObjectsManyClasses[Random.nextInt(0, testObjectCount)]).length.toLong())
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
        log("Iteration yielded ${PerformanceManyPropsWithPropSorting.charCount} characters")

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

        val callCountPerMethodPerIteration: Int = 1

        val testObjectCount: Int = testObjectsManyClasses.size
        private val propertyCount: Int = 5 // 5 properties per class
        val classCount: Int = testObjectsManyClasses.size // one instance per class

        private val plan: PerformanceSingleShot = PerformanceSingleShot()

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

        val charCount: AtomicLong = AtomicLong(0)

        init {
            log("""

            This test measures the performance of **`asString()`** compared to:
             * Apache's `ToStringBuilder.reflectionToString()`
             * `gson.toJson()`
             * IDE generated toString()

            Each method is called $callCountPerMethodPerIteration times per iteration, in 4 threads.
            Each class has a single object instance. The instances are randomly selected from the list.
            The count of tests is equal to the count of objects is; so on average, each object should be
            `asString()`-ed, `gson.toJson()`-ed, etc. once; but in practice, some objects will be hit a few times,
            while other objects will not be touched at all.

            The test comprises $testObjectCount objects, with $propertyCount*$classCount distinct `var` properties in $classCount classes.
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
    open fun asString(propsInstance: PropsToString): String = propsInstance.withAsString()

    @Benchmark
    open fun toStringBuilder(propsInstance: PropsToString): String = propsInstance.withToStringBuilder()

    @Benchmark
    open fun gsonToJson(propsInstance: PropsToString): String = propsInstance.withGson()

    @Benchmark
    open fun ideGeneratedToString(propsInstance: PropsToString): String = propsInstance.withIdeGeneratedToString()
}