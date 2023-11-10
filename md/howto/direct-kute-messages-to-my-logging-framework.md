| [← 🏠](../../)            | [← README.md](../../README.md) |
|:--------------------------|:-------------------------------|
| [← How to...](0-howto.md) | [→ FAQ](../../md/faq/0-faq.md) |

<hr>

## How to have Kute's error messages directed to a log framework

One of the basic ideas of **Kute** `asString()` is the principle of _zero-dependencies_.
> See [README.md →](../../README.md#basic-ideas-of-kutes-asstring)

A consequence is, that **Kute** can not use a decent logging framework, like an _SLF4J_-compatible logger.

### How does Kute `asString()` log error messages?
In general, **Kute** `asString()` should produce very little logging.<br>
But exceptions may happen, for sure.

If **Kute** `asString()` behaves unexpectedly, you may want to know what happened.<br>
So **Kute** has its own log-method, which, by default, simply writes to `System.out`.

### Redirect Kute `asString()`'s log messages to your logging system
It is easy to redirect **Kute** `asString()`'s log messages to your logging system instead,<br>
e.g. to an _SLF4J_-logger
(also documented in the [API documentation →](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.logging/logger.html)

<hr>

### Kotlin
**Kute** has a mutable public property `logger`:
```
var logger: (String?) -> Unit
```

As shown in this snippet, you can simply have your messages directed to your logging system<br>
by re-assigning `logger`, something like this:
```
private val kuteLogger = Logger.getLogger("nl.kute")
nl.kute.logging.logger = { msg -> kuteLogger.error(msg) }
```
When the `logger` is reassigned, **Kute** <u>checks</u> if the lambda being injected doesn't throw an `Exception`,<br>
by executing the lambda with an empty String.
> If an `Exception` would occur:
> * the new value is rejected
> * the existing logger is left in place
> * the `Exception` is logged (to the existing logger) with an informative message

<hr>

### Java
Working with Kotlin consumer-type lambdas is not really convenient in Java.<br>
Java programmers may want to use a different method, like this:

```
Logger myLogger = Logger.getLogger("nl.kute")
Consumer<String> kuteErrorLogger = msg -> myLogger.error(msg);
nl.kute.logging.KuteLogConsumer.setLogConsumer(kuteErrorLogger);
```

The same goes here: the logger is checked first, and if an `Exception` would occur:
> * the new value is rejected
> * the existing logger is left in place
> * the `Exception` is logged (to the existing logger) with an informative message
