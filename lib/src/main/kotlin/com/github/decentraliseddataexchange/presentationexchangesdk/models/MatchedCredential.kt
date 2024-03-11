package com.github.decentraliseddataexchange.presentationexchangesdk.models

/**
 * Matched path
 *
 * @constructor Create empty Matched path
 * @property path
 * @property index
 * @property match
 */
data class MatchedPath(
    val path: String?,
    val index: Int,
    val value: Any?
)

/**
 * Matched field
 *
 * @constructor Create empty Matched field
 * @property index
 * @property path
 */
data class MatchedField(
    val index: Int,
    val path: MatchedPath
)

/**
 * Matched credential
 *
 * When serialised to JSON string it will look like below:
 * { "index": 0, "fields": [ { "index": 0, "path": { "path": "$.type", "index": 0, "match": "Passport" } } ] }
 *
 * @constructor Create empty Matched credential
 * @property index
 * @property fields
 */
data class MatchedCredential(
    val index:  Int,
    val fields: List<MatchedField>
)

