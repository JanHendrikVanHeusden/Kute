package nl.kute.reflection.error

import nl.kute.log.log
import nl.kute.log.logger
import nl.kute.reflection.declaringClass
import nl.kute.util.asString
import java.lang.reflect.InaccessibleObjectException
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KProperty

private var illegalAccessReported: Boolean = false
private var inaccessibleObjectReported: Boolean = false

private val illegalAccessInfo: String =
    """ This may be caused by a security manager or by a java.policy that block access:
       |  * security manager may be enabled by default on some Linux Java distros,
       |    or be enabled explicitly by setting `-Djava.security.manager` or by `System.setSecurityManager(...)`
       |      * Check with `System.getSecurityManager()`
       |  * access may be restricted by java.policy
       |      * by default in `<java.home>/lib/security/java.policy`, may be overridden by `<user.home>/.java.policy`;
       |        or in location set by `--Djava.security.policy==/my/policy-file`
       | ---""".trimMargin()

private val inaccessibleObjectInfo: String =
    """ You may want either to:
       |  * export your module to `nl.kute` (preferred) to allow reflective access (with Osgi or Jigsaw named modules)
       |  * set the JVM's illegal access flag to `--illegal-access=permit` or `--illegal-access=warn`
       |    (not working anymore in Java 17+ see https://openjdk.org/jeps/403)
       |  * set the JVM's `--add-opens` option for the required packages
       | ---""".trimMargin()

@JvmSynthetic // avoid access from external Java code
internal fun KProperty<*>?.handlePropValException(exception: Exception) {
    val baseErrMsg: String =
        try {
            "${exception::class.simpleName} occurred when retrieving value of property [${this?.declaringClass()?.simpleName}.${this?.name}]; exception: ${exception.asString()}"
        } catch (t: Throwable) {
            "${exception::class} occurred when retrieving value of property [${this?.name}]; exception message: ${exception.message}; cause: ${exception.cause?.javaClass}"
        }
    try {
        when (exception) {
            is IllegalAccessException -> {
                // This may happen when a security manager blocks property access
                if (!illegalAccessReported) {
                    log(
                        """$baseErrMsg
                | $illegalAccessInfo
                | Objects that are not accessible will be represented as `null`. Maybe a security manager blocks it.
                | This warning is shown only once.""".trimMargin()
                    )
                    illegalAccessReported = true
                }
            }

            is InaccessibleObjectException -> {
                // This may happen when using named modules (Java Jigsaw) that do not allow deep reflective access
                if (!inaccessibleObjectReported) {
                    log(
                        """$baseErrMsg
                | $inaccessibleObjectInfo
                | Objects that are not accessible will be represented as `null`.
                | Maybe a module-system (Osgi, Jigsaw [Java Platform Module System]) blocks it.
                | This warning is shown only once.""".trimMargin()
                    )
                    inaccessibleObjectReported = true
                }
            }

            is InvocationTargetException -> {
                if (this?.isLateinit == true && exception.cause is UninitializedPropertyAccessException) {
                    // lateinit property not yet initialized, no need to print error message. Just consider it null.
                } else {
                    log(baseErrMsg)
                }
            }

            else -> {
                log(baseErrMsg)
            }
        }
    } catch (e1: Exception) {
        // Should never happen - just as a last resort
        try {
            logger.invoke(baseErrMsg)
        } catch (e2: Exception) {
            // ignore
        }
        e1.printStackTrace()
    }
}