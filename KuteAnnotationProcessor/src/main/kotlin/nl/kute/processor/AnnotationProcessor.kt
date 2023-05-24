package nl.kute.processor

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate
import nl.kute.printable.annotation.NoPrintHash
import nl.kute.printable.annotation.NoPrintMask
import nl.kute.printable.annotation.NoPrintOmit
import nl.kute.printable.annotation.NoPrintPatternReplace
import nl.kute.printable.annotation.PrintOption
import kotlin.reflect.KClass

private const val packageNameGeneratedCode = "nl.kute.sample.generated"

class AnnotationProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    // WIP Sample processor that may produce a Kotlin file, but not something really meaningful
    // (modified from sample of https://proandroiddev.com/the-guide-to-your-first-annotation-processor-with-ksp-and-becoming-a-kotlin-artist-4e5d13f171e6)
    override fun process(resolver: Resolver): List<KSAnnotated> {

        val annotatedElements: Sequence<KSAnnotated>? = resolver.findAnnotations(
            PrintOption::class,
            NoPrintOmit::class,
            NoPrintMask::class,
            NoPrintHash::class,
            NoPrintPatternReplace::class,
        )
        if (annotatedElements?.iterator()?.hasNext() != true) {
            return emptyList()
        }
        val elementNames = annotatedElements.map { it.toString() }

        val sourceFiles = annotatedElements.mapNotNull { it.containingFile }
        val fileText = buildString {
            append("package $packageNameGeneratedCode\n\n")
            append("// ")
            append(elementNames.joinToString(", "))
        }
        val file = environment.codeGenerator.createNewFile(
            Dependencies(false, *sourceFiles.toList().toTypedArray()),
            packageNameGeneratedCode,
            "ProcessedAnnotations"
        )

        file.write(fileText.toByteArray())
        return (annotatedElements).filterNot { it.validate() }.toList()
    }

    private fun Resolver.findAnnotations(vararg kClasses: KClass<*>): Sequence<KSAnnotated>? {
        var aggregate: Sequence<KSAnnotated>? = null
        kClasses.forEach { kClass ->
            with(getSymbolsWithAnnotation(kClass.qualifiedName.toString())) {
                aggregate = if (aggregate == null) { this } else { aggregate!! + this }
            }
        }
        return aggregate
    }
}
