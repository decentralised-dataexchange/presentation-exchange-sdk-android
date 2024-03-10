package com.github.decentraliseddataexchange.presentationexchangesdk

import net.jimblackler.jsonschemafriend.ValidationException
import kotlin.test.Test
import kotlin.test.assertFailsWith

class PresentationExchangeTest {

    @Test
    fun `valid JSON should not throw exception`() {
        val presentationExchange = PresentationExchange()

        // JSON schema for testing with an array type.
        val jsonSchema = (
                "{"
                        + "  \"\$schema\": \"http://json-schema.org/draft-07/schema#\","
                        + "  \"type\": \"array\""
                        + "}")

        // Set the schema in the PresentationExchange instance.
        presentationExchange.setSchema(jsonSchema)

        // No exception should be thrown for valid input.
        // Note: input is now a JSON array, represented as a string.
        val input = "[\"Passport\"]"
        presentationExchange.validateJsonSchema(input)
    }

    @Test
    fun `invalid JSON should throw ValidationException`() {
        val presentationExchange = PresentationExchange()
        // JSON schema for testing.
        val jsonSchema = ("{"
                + "  \"\$schema\": \"http://json-schema.org/draft-07/schema#\","
                + "  \"type\": \"integer\""
                + "}")
        // Set the schema in the PresentationExchange instance.
        presentationExchange.setSchema(jsonSchema)
        // An exception of type ValidationException should be thrown for invalid input.
        val input = "true"
        assertFailsWith<ValidationException> {
            presentationExchange.validateJsonSchema(input)
        }
    }
}