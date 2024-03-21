package com.github.decentraliseddataexchange.presentationexchangesdk.models


/**
 * Input descriptor
 *
 * @constructor Create empty Input descriptor
 * @property id
 * @property constraints
 */
data class InputDescriptor(
    val id: String,
    val constraints: Constraints
)


/**
 * Constraints
 *
 * @constructor Create empty Constraints
 * @property fields
 */
data class Constraints(
    val fields: List<Field>,
    val limit_disclosure: String?
)


/**
 * Field
 *
 * @constructor Create empty Field
 * @property path
 * @property filter
 */
data class Field(
    val path: List<String>,
    val filter: Any?,
    val optional: Boolean?
)





