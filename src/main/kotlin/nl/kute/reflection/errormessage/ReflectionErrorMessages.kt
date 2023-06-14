package nl.kute.reflection.errormessage

val illegalAccessInfo: String =
    """ This may be caused by a security manager or by a java.policy that block access:
       |  * security manager may be enabled by default on some Linux Java distros,
       |    or be enabled explicitly by setting `-Djava.security.manager` or by `System.setSecurityManager(...)`
       |      * Check with `System.getSecurityManager()`
       |  * access may be restricted by java.policy
       |      * by default in `<java.home>/lib/security/java.policy`, may be overridden by `<user.home>/.java.policy`;
       |        or in location set by `--Djava.security.policy==/my/policy-file`
       | ---""".trimMargin()

val inaccessibleObjectInfo: String =
    """ You may want either to:
       |  * export your module to `nl.kute` (preferred) to allow reflective accesss (with Jigsaw named modules)
       |  * set the JVM's illegal access flag to `--illegal-access=permit` or `--illegal-access=warn`
       |    (not working anymore in Java 17+ see https://openjdk.org/jeps/403)
       |  * set the JVM's `--add-opens` option for the required packages
       | ---""".trimMargin()
