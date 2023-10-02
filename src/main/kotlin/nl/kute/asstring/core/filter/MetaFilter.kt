package nl.kute.asstring.core.filter

import nl.kute.asstring.property.meta.ClassMeta
import nl.kute.asstring.property.meta.PropertyMeta

/**
 * Alias for type `(`[PropertyMeta]`)` -> [Boolean]
 * @see [nl.kute.asstring.config.AsStringConfig.withPropertyOmitFilters]
 */
public typealias PropertyMetaFilter = (PropertyMeta) -> Boolean

/**
 * Alias for type `(`[ClassMeta]`)` -> [Boolean]
 * @see [nl.kute.asstring.config.AsStringConfig.withForceToStringFilters]
 */
public typealias ClassMetaFilter = (ClassMeta) -> Boolean