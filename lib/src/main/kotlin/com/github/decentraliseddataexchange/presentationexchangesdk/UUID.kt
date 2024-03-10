package com.github.decentraliseddataexchange.presentationexchangesdk

import java.util.UUID

fun generateUUID4(): String {
    val uuid = UUID.randomUUID()
    return uuid.toString()
}