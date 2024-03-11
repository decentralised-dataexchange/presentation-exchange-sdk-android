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


    /**
     * Normalise combine format for issuance to json string
     *
     * @param claimJson
     * @param disclosureBase64s
     * @return
     */
    fun normaliseCombineFormatForIssuanceToJsonString(claimJson: String, disclosureBase64s: List<String>): String
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
     * Match credentials
     *
     * @param inputDescriptorJson input descriptor as JSON string
     * @param credentials credentials to be matched against the input
     *     descriptor
     * @return
     */
    override fun matchCredentials(inputDescriptorJson: String, credentials: List<String>): List<MatchedCredential> {
        // Note: If limited_disclosure = true (SD-JWT),
        // then only the fields specified in the constraints should be present in SD-JWT-R
        val inputDescriptor = this.deserialiseInputDescriptor(inputDescriptorJson)
        val gson = Gson()
        val matchedCredentials = mutableListOf<MatchedCredential>()
        // Iterate through credentials
        credentialLoop@ for ((credentialIndex, credential) in credentials.withIndex()) {
            // Iterate through fields specified in the constraints
            // Assume credential matches until proven otherwise
            var credentialMatched = true
            // Matched field
            val matchedFields = mutableListOf<MatchedField>()
            fieldLoop@ for ((fieldIndex, field) in inputDescriptor.constraints.fields.withIndex()) {
                // If optional = true, then if the validation fails, the field can be skipped
                // Iterate through JSON paths
                // If multiple paths are provided, then match at-least one
                var fieldMatched = false
                for ((pathIndex, path) in field.path.withIndex()) {
                    try {
                        val matchedPathValue = JsonPath.read<Any>(credential, path)
                        val matchedJson = gson.toJson(matchedPathValue)
                        if (
                            field.filter != null &&
                            !this.validateJsonSchema(matchedJson, field.filter.toJsonSchemaString())
                        ) {
                            // If filter is present and validation fails, move to the next credential
                            credentialMatched = false
                            break@fieldLoop
                        }
                        // Move to the next field if current path matches
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
                    credentialMatched = false
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

    override fun normaliseCombineFormatForIssuanceToJsonString(
        claimJson: String,
        disclosureBase64s: List<String>
    ): String {
        TODO("Not yet implemented")
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
