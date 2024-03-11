package com.github.decentraliseddataexchange.presentationexchangesdk

import com.github.decentraliseddataexchange.presentationexchangesdk.models.MatchedCredential
import com.google.gson.Gson


fun main() {
    val pex = PresentationExchange()
    val inputDescriptor =
        """{"id":"9a18d1b5-13ac-4fbc-8c12-d5916740ce1d","constraints":{"fields":[{"path":["${'$'}.address.city"]}]}}"""
    val credentialsList = listOf(
        """{"type":["Passport"],"name":"John","dob":"14-Mar-70","address":{"city":"EKM","state":"Kerala"}}""",
    )
    val matches: List<MatchedCredential> = pex.matchCredentials(inputDescriptor, credentialsList)
    println(Gson().toJson(matches))
}