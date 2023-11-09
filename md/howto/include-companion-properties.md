| [← 🏠](../../)            | [← README.md](../../README.md) |
|:--------------------------|:-------------------------------|
| [← How to...](0-howto.md) | [→ FAQ](../../md/faq/0-faq.md) |

<hr>

## Include companion properties
By default, companion properties are not added in output of asString().<br>
You can opt however to have these included.

### Restrictions
Due to restrictions of Kotlin's reflection, a companion object's properties are shown only if:

1. The class that contains the companion object must not be private
2. The companion object must be public
3. The companion object has at least 1 property

### Include companion object for a single class
To include a companion object for a single class, just annotate your class with <br>
`@AsStringClassOption(includeCompanion = true)`<br>

### Include companion object globally
Use this snippet:
```
AsStringConfig()
    .withIncludeCompanion(true)
    .applyAsDefault()
```
See [Configure default settings](configure-default-settings.md) for more on applying settigs application-wide.
