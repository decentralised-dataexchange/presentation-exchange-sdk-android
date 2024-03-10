package com.github.decentraliseddataexchange.presentationexchangesdk

import com.github.decentraliseddataexchange.presentationexchangesdk.models.DescriptorMap
import com.github.decentraliseddataexchange.presentationexchangesdk.models.InputDescriptor
import com.github.decentraliseddataexchange.presentationexchangesdk.models.PathNested
import com.google.gson.Gson
import com.jayway.jsonpath.JsonPath
import net.jimblackler.jsonschemafriend.Schema
import net.jimblackler.jsonschemafriend.SchemaStore
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
    fun matchCredentials(inputDescriptorJson: String, credentials: List<String>): List<DescriptorMap>
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
    override fun matchCredentials(inputDescriptorJson: String, credentials: List<String>): List<DescriptorMap> {
        // TODO: The response should be blueprint to create descriptor map and SD-JWTs/JWTs in VP token
        // TODO: If limited_disclosure = true (SD-JWT), then only the fields specified should be present in the presentation
        val inputDescriptor = this.deserialiseInputDescriptor(inputDescriptorJson)
        val gson = Gson()
        val matches = mutableListOf<DescriptorMap>()
        // Iterate through credentials
        for (credential in credentials) {
            // Iterate through fields specified in the constraints
            // TODO: All the field filters should be validated against the filter, if one of them fails, then credential is not matched
            // TODO: If optional = true, then if the validation fails, the field can be skipped
            for (field in inputDescriptor.constraints.fields) {
                // Iterate through JSON paths
                // TODO: If either of the paths are available proceed as multiple paths are specified as an alternative
                for (path in field.path) {
                    // Validate JSON path and fetch the match
                    val matched = JsonPath.read<List<String>>(credential, path)
                    val matchedJson = gson.toJson(matched)
                    // Validate JSON schema
                    this.validateJsonSchema(matchedJson, field.filter.toJsonSchemaString())
                    // Add the matched result to the list
                    matches.add(
                        DescriptorMap(
                            format = "jwt_vp",
                            path = "$",
                            id = inputDescriptor.id,
                            pathNested = PathNested(
                                format = "jwt_vc",
                                id = generateUUID4(),
                                path = "$.vc[0]"
                            )
                        )
                    )
                }
            }
        }

        // Return the list of matched results
        return matches
    }

    /**
     * Validate json schema
     *
     * @param input json input
     * @param jsonSchemaStr json schema for validating the input
     */
    private fun validateJsonSchema(input: String, jsonSchemaStr: String) {
        // Initialize a SchemaStore.
        val schemaStore = SchemaStore()
        // Load the schema.
        val schema: Schema = schemaStore.loadSchemaJson(jsonSchemaStr)
        // Create a validator.
        val validator = Validator()
        // Validate JSON based on the input, if failed will throw ValidationException
        validator.validateJson(schema, input)
    }
}
