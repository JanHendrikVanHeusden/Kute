@file:Suppress("KDocMissingDocumentation")

package nl.kute.performance

import nl.kute.asstring.annotation.additionalAnnotations
import nl.kute.asstring.annotation.modify.cachingRegexFactory
import nl.kute.asstring.annotation.option.PropertyValueSurrounder
import nl.kute.asstring.annotation.option.ToStringPreference
import nl.kute.asstring.annotation.option.asStringClassOptionCache
import nl.kute.asstring.core.PropertyMetaFilter
import nl.kute.asstring.config.asStringConfig
import nl.kute.asstring.config.restoreInitialAsStringClassOption
import nl.kute.asstring.config.restoreInitialAsStringOption
import nl.kute.asstring.core.objectCategoryCache
import nl.kute.asstring.core.useToStringByClass
import nl.kute.asstring.property.propsWithAnnotationsCacheByClass
import nl.kute.asstring.property.ranking.PropertyRankingByStringValueLength
import nl.kute.log.log
import nl.kute.performance.PerformanceFewPropsLotsOfOptions.Companion.callCountPerMethodPerIteration
import nl.kute.reflection.util.classToStringMethodCache
import nl.kute.testobjects.performance.PropsToString
import nl.kute.testobjects.performance.modifyFewPropValues
import nl.kute.testobjects.performance.propClassesFewProps
import nl.kute.testobjects.performance.propListFewPropsAll
import nl.kute.testobjects.performance.testObjectsFewProps
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.TearDown
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

// Set to `true` to enable the test
private var enabled = true

internal class Dummy

/**
 * Runs the same tests as [PerformanceFewProps], but with property sorting (i.e.,
 * properties with their String-values are ordered by specified criteria in the output of [nl.kute.asstring.core.asString])
 *
 *  * *Run gradle task *jmh* -> `jmh` to execute this test.*
 *  * Or run it using the generated jar: `build/libs/Kute-<version>-jmh.jar`
 *  * Alternatively, run it from your IDE if you can find a plugin that does the job
 *
 * The test typically runs in less than 5 minutes, depending on hardware & environment
 * > With [callCountPerMethodPerIteration] = 1000
 *  * If running it on a laptop, make sure it does not enter sleep mode.
 *     * on Mac you may run the command<br>
 *       `caffeinate -d -t 1800`
 *       <br>to keep it awake for half an hour (1800s)
 *     * on Linux and Windows, Google and/or consult documentation on how to prevent sleep mode
 *
 *  Total duration of the Gradle `jmh` task may take about 40 minutes if all tests are enabled.
 */
@State(Scope.Benchmark)
open class PerformanceFewPropsLotsOfOptions {

    @Setup(Level.Iteration)
    fun setUpIteration() {
        if (!enabled) {
            throw(IllegalStateException(disabledWarning))
        }
        val propertyFilter : PropertyMetaFilter = { meta ->
            // won't filter out anything, but it will be checked on each asString() call
            meta.returnType.classifier == Dummy::class
        }
        asStringConfig()
            .withPropertiesAlphabetic(true)
            .withPropertySorters(PropertyRankingByStringValueLength::class)
            .withSurroundPropValue(PropertyValueSurrounder.`«»`)
            .withIncludeIdentityHash(true)
            .withToStringPreference(ToStringPreference.PREFER_TOSTRING)
            .withPropertyOmitFilters(propertyFilter)
            .applyAsDefault()

        testObjectsFewProps.modifyFewPropValues()
        var charCount: Long = 0
        repeat(callCountPerMethodPerIteration) {
            charCount += asStringTask(testObjectsFewProps[Random.nextInt(0, testObjectCount)]).length.toLong()
        }

        // charCount: just to make sure that the JVM does not eliminate the asString()/ToStringBuilder/gson/ideToString calls
        log("Iteration yielded $charCount characters")
        log("""# of executions (total over iterations):
               | asString       : $asStringExecutionCount
        """.trimMargin())

    }

    @TearDown(Level.Trial)
    fun tearDownPerformanceTest() {
        restoreInitialAsStringClassOption()
        restoreInitialAsStringOption()

        if (!enabled) {
            throw(IllegalStateException(disabledWarning))
        }

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

        val testObjectCount: Int = testObjectsFewProps.size
        private val propertyCount: Int = propListFewPropsAll.size
        private val classCount: Int = propClassesFewProps.size

        private val plan: PerformanceFewPropsLotsOfOptions = PerformanceFewPropsLotsOfOptions()

        val asStringExecutionCount: AtomicInteger = AtomicInteger(0)
        val asStringTask: ToStringTask = { p ->
            asStringExecutionCount.incrementAndGet()
            plan.asString(p)
        }

        init {
            log("""

            This test measures the performance of **`asString()`**, with several additional settings.

            `asString()` is called $callCountPerMethodPerIteration times per iteration, in 4 threads.

            The test comprises $testObjectCount objects, with $propertyCount distinct `var` properties in $classCount classes.
            Between runs of the tests, the property values are updated.
            Each run of the test runs the various options (Kute, IDE-generated, Apache, Gson) in different (random) order.

            """.trimIndent())

            log("""

            The test typically runs in less than 5 minutes, depending on hardware & environment.
            If running it on a laptop, make sure it does not enter sleep mode.
             * on Mac you may run the command `caffeinate -d -t 1800` to keep it awake for half an hour (1800s)
             * on Linux and Windows, Google and/or consult documentation

        """.trimIndent())
        }
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.All)
    open fun asString(propsInstance: PropsToString): String = propsInstance.withAsString()

}