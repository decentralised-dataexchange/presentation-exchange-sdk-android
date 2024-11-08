package com.github.decentraliseddataexchange.presentationexchangesdk

import com.github.decentraliseddataexchange.presentationexchangesdk.models.*
import com.google.gson.Gson
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import net.jimblackler.jsonschemafriend.Schema
import net.jimblackler.jsonschemafriend.SchemaStore
import net.jimblackler.jsonschemafriend.ValidationException
import net.jimblackler.jsonschemafriend.Validator


interface IPresentationExchange {
    /**
     * Match credentials
     *
     * @param inputDescriptorJson input descriptor as JSON string
     * @param credentials credentials to be matched against the input
     *     descriptor
     * @return
     */
    fun matchCredentials(inputDescriptorJson: String, credentials: List<String>): List<MatchedCredential>
}


class PresentationExchange() : IPresentationExchange {

    /**
     * Deserialise input descriptor
     *
     * @param inputDescriptorJson input descriptor as JSON string
     * @return an instance of <InputDescriptor>
     */
    private fun deserialiseInputDescriptor(inputDescriptorJson: String): InputDescriptor {
        val gson = Gson()
        // Deserialise JSON string into <InputDescriptor>
        val jsonData = gson.fromJson(inputDescriptorJson, InputDescriptor::class.java)
        return jsonData
    }

    /**
     * To json schema string
     *
     * @param input
     * @return Json string
     */
    private fun toJsonSchemaString(input: Any): String {
        val gson = Gson()
        val jsonSchema = gson.toJsonTree(input).asJsonObject
        jsonSchema.addProperty("\$schema", "http://json-schema.org/draft-07/schema#")
        return gson.toJson(jsonSchema)
    }

    /**
     * Match credentials
     *
     * @param inputDescriptorJson input descriptor as JSON string
     * @param credentials credentials to be matched against the input
     *     descriptor
     * @return
     */
    override fun matchCredentials(inputDescriptorJson: String, credentials: List<String>): List<MatchedCredential> {
        // Deserialize input descriptor JSON string into InputDescriptor object
        val inputDescriptor = this.deserialiseInputDescriptor(inputDescriptorJson)
        val gson = Gson()
        val matchedCredentials = mutableListOf<MatchedCredential>()

        // Iterate through each credential
        credentialLoop@ for ((credentialIndex, credential) in credentials.withIndex()) {
            // Assume credential matches until proven otherwise
            var credentialMatched = true
            val matchedFields = mutableListOf<MatchedField>()

            // Iterate through fields specified in the constraints
            fieldLoop@ for ((fieldIndex, field) in inputDescriptor.constraints.fields.withIndex()) {
                // Assume field matches until proven otherwise
                var fieldMatched = false

                // Iterate through JSON paths for the current field
                for ((pathIndex, path) in field.path.withIndex()) {
                    try {
                        // Attempt to read the JSON path from the credential
                        val matchedPathValue = JsonPath.read<Any>(credential, path)
                        val matchedJson = gson.toJson(matchedPathValue)

                        // Validate the matched JSON against the field's filter
                        if (field.filter != null && !this.validateJsonSchema(matchedJson, toJsonSchemaString(field.filter))) {
                            // If filter is present and validation fails, move to the next credential
                            credentialMatched = false
                            break@fieldLoop
                        }

                        // Add the matched field to the list
                        fieldMatched = true
                        matchedFields.add(
                            MatchedField(
                                index = fieldIndex,
                                path = MatchedPath(
                                    index = pathIndex,
                                    path = path,
                                    value = matchedPathValue
                                )
                            )
                        )
                        break
                    } catch (e: PathNotFoundException) {
                        // If path didn't match, then move to next path
                        println(e.stackTraceToString())
                    }
                }

                if (!fieldMatched) {
                    // If any one field didn't match then move to next credential
                    if (field.optional != true) {
                        credentialMatched = false
                    }
                    break@fieldLoop
                }
            }

            if (credentialMatched) {
                // All fields matched, then credential is matched
                matchedCredentials.add(
                    MatchedCredential(
                        index = credentialIndex,
                        fields = matchedFields
                    )
                )
            }
        }

        // Return the list of matched credentials
        return matchedCredentials
    }

    /**
     * Validate json schema
     *
     * @param input json input
     * @param jsonSchemaStr json schema for validating the input
     */
    private fun validateJsonSchema(input: String, jsonSchemaStr: String): Boolean {
        try {
            // Initialize a SchemaStore.
            val schemaStore = SchemaStore()
            // Load the schema.
            val schema: Schema = schemaStore.loadSchemaJson(jsonSchemaStr)
            // Create a validator.
            val validator = Validator()
            // Validate JSON based on the input, if failed will throw ValidationException
            validator.validateJson(schema, input)
            return true
        } catch (e: ValidationException) {
            return false
        }

    }
}
