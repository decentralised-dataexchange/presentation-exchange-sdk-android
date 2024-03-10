package com.github.decentraliseddataexchange.presentationexchangesdk.models

/**
 * Descriptor map
 *
 * @constructor Create empty Descriptor map
 * @property format
 * @property id
 * @property path
 * @property pathNested
 */
data class DescriptorMap(
    val format: String,
    val id: String,
    val path: String,
    val pathNested: PathNested
)

/**
 * Path nested
 *
 * @constructor Create empty Path nested
 * @property format
 * @property id
 * @property path
 */
data class PathNested(
    val format: String,
    val id: String,
    val path: String
)



