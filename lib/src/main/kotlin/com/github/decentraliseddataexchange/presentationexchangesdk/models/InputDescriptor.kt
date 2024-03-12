package com.github.decentraliseddataexchange.presentationexchangesdk.models

import com.google.gson.Gson


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
    val filter: Filter?,
    val optional: Boolean?
)


/**
 * Filter
 *
 * @constructor Create empty Filter
 * @property type
 * @property contains
 */
data class Filter(
    val type: String,
    val contains: Contains? =null,
    val const: String? = null,
    val pattern:String? = null
) {
    /**
     * To json schema string
     *
     * @return
     */
    fun toJsonSchemaString(): String {
        val gson = Gson()
        val jsonSchema = gson.toJsonTree(this).asJsonObject
        jsonSchema.addProperty("\$schema", "http://json-schema.org/draft-07/schema#")
        return gson.toJson(jsonSchema)
    }
}


/**
 * Contains
 *
 * @constructor Create empty Contains
 * @property const
 */
data class Contains(
    val const: String
)





