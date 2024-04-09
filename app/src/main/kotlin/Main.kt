package com.github.decentraliseddataexchange.presentationexchangesdk

import com.github.decentraliseddataexchange.presentationexchangesdk.models.MatchedCredential
import com.google.gson.Gson


fun main() {
    val pex = PresentationExchange()
    val inputDescriptor =
        """{"id":"abd4acb1-1dcb-41ad-8596-ceb1401a69c7","format":{"vc+sd-jwt":{"alg":["ES256","ES384"]}},"constraints":{"fields":[{"path":["${'$'}.given_name"]},{"path":["${'$'}.last_name"]},{"path":["${'$'}.vct"],"filter":{"type":"string","const":"VerifiablePortableDocumentA1"}}]},"limit_disclosure":"required"}"""
    val credentialsList = listOf(
        """{"iss":"https://dss.aegean.gr/rfc-issuer","iat":1712657569263,"vct":"VerifiablePortableDocumentA1","given_name":"John","last_name":"Doe"}""",
    )
    val matches: List<MatchedCredential> = pex.matchCredentials(inputDescriptor, credentialsList)
    println(Gson().toJson(matches))
}