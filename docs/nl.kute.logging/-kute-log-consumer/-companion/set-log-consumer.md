---
title: setLogConsumer
---
//[kute](../../../../index.html)/[nl.kute.logging](../../index.html)/[KuteLogConsumer](../index.html)/[Companion](index.html)/[setLogConsumer](set-log-consumer.html)



# setLogConsumer



[jvm]\




@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)



fun [setLogConsumer](set-log-consumer.html)(aLogger: [Consumer](https://docs.oracle.com/javase/8/docs/api/java/util/function/Consumer.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?&gt;)



[setLogConsumer](set-log-consumer.html) can be used in **Java** code to set a different logger.



By default, wen no other logger set explicitly, Kute uses stdOutLogger to output to std out (using `println()`).



A different logger (typically SLF4J etc.) can be injected to have it send error logs to your logging framework.



- 
   To be used from within **Java**
- 
   In **Kotlin** code, use [logger](../../logger.html) instead




More convenient in **Java** than hassling with [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) (as you would with [logger](../../logger.html))



Typical usage for Java with an *SLF4J* compatible logger would be:

```kotlin
Logger myLogger = Logger.getLogger("nl.kute")
Consumer<String> kuteErrorLogger = msg -> myLogger.error(msg);
nl.kute.logging.KuteLogConsumer.setLogConsumer(kuteErrorLogger);
```


#### See also


| |
|---|
| [logger](../../logger.html) |



