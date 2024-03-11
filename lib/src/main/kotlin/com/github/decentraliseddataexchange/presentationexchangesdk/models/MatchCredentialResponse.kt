package com.github.decentraliseddataexchange.presentationexchangesdk.models

data class SdJwtField(
    val index: Int,
    val jsonPathMatched: String
)

data class MatchedCredentials(
    val sdJwtField: List<SdJwtField>,
    val vc: Any
)

// { "matchedCredentials": [ { "fields":  [ { "index": 0, "jsonPathMatch": "[]" } ], "vc": "Passport" } ]

data class MatchCredentialResponse(
    val descriptorMap:  List<DescriptorMap>,
    val matchedCredentials:  List<MatchedCredentials>
)
