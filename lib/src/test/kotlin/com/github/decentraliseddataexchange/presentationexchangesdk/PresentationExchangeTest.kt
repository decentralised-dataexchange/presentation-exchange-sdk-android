package com.github.decentraliseddataexchange.presentationexchangesdk

import com.github.decentraliseddataexchange.presentationexchangesdk.models.MatchedCredential
import net.jimblackler.jsonschemafriend.ValidationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PresentationExchangeTest {

    @Test
    fun `find match credentials with fields type, name, dob and city matching Passport, John, 14-Mar-70 and EKM respectively`() {
        val pex = PresentationExchange()
        val inputDescriptor =
            """{"id":"9a18d1b5-13ac-4fbc-8c12-d5916740ce1d","constraints":{"fields":[{"path":["$.type"],"filter":{"type":"array","contains":{"const":"Passport"}}},{"path":["$.name"],"filter":{"type":"string","const":"John"}},{"path":["$.dob"],"filter":{"type":"string","const":"14-Mar-70"}},{"path":["$.address.city"],"filter":{"type":"string","const":"EKM"}}]}}"""
        val credentialsList = listOf(
            """{"type":["Passport"],"name":"John","dob":"14-Mar-70","address":{"city":"EKM","state":"Kerala"}}""",
        )
        val matches: List<MatchedCredential> = pex.matchCredentials(inputDescriptor, credentialsList)

        assertEquals(1, matches.size)
        assertEquals(4, matches[0].fields.size)
    }

    @Test
    fun `find matching credentials with field city`() {
        val pex = PresentationExchange()
        val inputDescriptor =
            """{"id":"9a18d1b5-13ac-4fbc-8c12-d5916740ce1d","constraints":{"fields":[{"path":["$.address.city"]}]}}"""
        val credentialsList = listOf(
            """{"type":["Passport"],"name":"John","dob":"14-Mar-70","address":{"city":"EKM","state":"Kerala"}}""",
            """{"type":["Passport"],"name":"Alice","dob":"14-Mar-80","address":{"city":"Stockholm","state":"Stockholm"}}"""
        )
        val matches: List<MatchedCredential> = pex.matchCredentials(inputDescriptor, credentialsList)

        assertEquals(2, matches.size)
        assertEquals(1, matches[0].fields.size)
        assertEquals("$.address.city", matches[0].fields[0].path.path)
        assertEquals("EKM", matches[0].fields[0].path.value)

        assertEquals(1, matches[1].fields.size)
        assertEquals("$.address.city", matches[1].fields[0].path.path)
        assertEquals("Stockholm", matches[1].fields[0].path.value)
    }

    @Test
    fun `find matching credentials with field age and value should a integer`() {
        val pex = PresentationExchange()
        val inputDescriptor =
            """{"id":"9a18d1b5-13ac-4fbc-8c12-d5916740ce1d","constraints":{"fields":[{"path":["$.age"], "filter":{"type":"integer"}}]}}"""
        val credentialsList = listOf(
            """{"age": 25}""",
        )

        val matches: List<MatchedCredential> = pex.matchCredentials(inputDescriptor, credentialsList)
        assertEquals(1, matches.size)
    }

    @Test
    fun `EWC RFC 002 verification testcase`() {
        val pex = PresentationExchange()
        val inputDescriptor =
            """{"id":"abd4acb1-1dcb-41ad-8596-ceb1401a69c7","format":{"vc+sd-jwt":{"alg":["ES256","ES384"]}},"constraints":{"fields":[{"path":["${'$'}.given_name"]},{"path":["${'$'}.last_name"]},{"path":["${'$'}.vct"],"filter":{"type":"string","const":"VerifiablePortableDocumentA1"}}]},"limit_disclosure":"required"}"""
        val credentialsList = listOf(
            """{"iss":"https://dss.aegean.gr/rfc-issuer","iat":1712657569263,"vct":"VerifiablePortableDocumentA1","given_name":"John","last_name":"Doe"}""",
        )
        val matches: List<MatchedCredential> = pex.matchCredentials(inputDescriptor, credentialsList)

        assertEquals(1, matches.size)
    }

    @Test
    fun  `find matched Credentials of field vct which is not present in credential and field optional value is true`(){
        val pex = PresentationExchange()
        val inputDescriptor =
            """{"id":"abd4acb1-1dcb-41ad-8596-ceb1401a69c7","format":{"vc+sd-jwt":{"alg":["ES256","ES384"]}},"constraints":{"fields":[{"path":["${'$'}.given_name"]},{"path":["${'$'}.last_name"]},{"path":["${'$'}.vct"],"filter":{"type":"string","const":"VerifiablePortableDocumentA1"},"optional":true}]},"limit_disclosure":"required"}"""
        val credentialsList = listOf(
            """{"iss":"https://dss.aegean.gr/rfc-issuer","iat":1712657569263,"given_name":"John","last_name":"Doe"}""",
        )
        val matches: List<MatchedCredential> = pex.matchCredentials(inputDescriptor, credentialsList)
        assertEquals(1, matches.size)
    }

    @Test
    fun  `find matched Credentials of field vct which is not present in credential and field optional value is false`(){
        val pex = PresentationExchange()
        val inputDescriptor =
            """{"id":"abd4acb1-1dcb-41ad-8596-ceb1401a69c7","format":{"vc+sd-jwt":{"alg":["ES256","ES384"]}},"constraints":{"fields":[{"path":["${'$'}.given_name"]},{"path":["${'$'}.last_name"]},{"path":["${'$'}.vct"],"filter":{"type":"string","const":"VerifiablePortableDocumentA1"},"optional":false}]},"limit_disclosure":"required"}"""
        val credentialsList = listOf(
            """{"iss":"https://dss.aegean.gr/rfc-issuer","iat":1712657569263,"given_name":"John","last_name":"Doe"}""",
        )
        val matches: List<MatchedCredential> = pex.matchCredentials(inputDescriptor, credentialsList)
        assertEquals(0, matches.size)
    }

    @Test
    fun  `find matched Credentials of field vct which is not present in credential and field optional is not present`(){
        val pex = PresentationExchange()
        val inputDescriptor =
            """{"id":"abd4acb1-1dcb-41ad-8596-ceb1401a69c7","format":{"vc+sd-jwt":{"alg":["ES256","ES384"]}},"constraints":{"fields":[{"path":["${'$'}.given_name"]},{"path":["${'$'}.last_name"]},{"path":["${'$'}.vct"],"filter":{"type":"string","const":"VerifiablePortableDocumentA1"}}]},"limit_disclosure":"required"}"""
        val credentialsList = listOf(
            """{"iss":"https://dss.aegean.gr/rfc-issuer","iat":1712657569263,"given_name":"John","last_name":"Doe"}""",
        )
        val matches: List<MatchedCredential> = pex.matchCredentials(inputDescriptor, credentialsList)
        assertEquals(0, matches.size)
    }
}