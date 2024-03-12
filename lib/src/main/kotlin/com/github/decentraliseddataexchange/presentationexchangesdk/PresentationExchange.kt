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
    fun matchCredentials(inputDescriptorJson: String, credentials: List<String>): MatchCredentialResponse


    /**
     * Normalise combine format for issuance to json string
     *
     * @param claimJson - from credential
     * @param disclosureBase64s - list of all disclosures
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
    override fun matchCredentials(inputDescriptorJson: String, credentials: List<String>): MatchCredentialResponse {
        // TODO: If limited_disclosure = true (SD-JWT), then only the fields specified in the constraints should be present in SD-JWT-R
        val inputDescriptor = this.deserialiseInputDescriptor(inputDescriptorJson)
        val gson = Gson()
        val matches = mutableListOf<DescriptorMap>()
        val matchedCredentials = mutableListOf<MatchedCredentials>()
        // Iterate through credentials
        val limitDisclosure = inputDescriptor.constraints?.limit_disclosure
        for (credential in credentials) {
            // Iterate through fields specified in the constraints
            // TODO: If optional = true, then if the validation fails, the field can be skipped
            if (limitDisclosure == null) {
                var isMatched = true
                for (field in inputDescriptor.constraints?.fields ?: listOf()) {
                    // Iterate through JSON paths
                    // TODO: remove the buildVariationsOfJsonPath function, once its stable
                    for (path in buildVariationsOfJsonPath(field.path)) {
                        // Validate JSON path and fetch the match
                        try {
                            val matched = JsonPath.read<Any>(credential, path)
                            val matchedJson = gson.toJson(matched)
                            // Validate JSON schema
                            try {
                                this.validateJsonSchema(matchedJson, field.filter.toJsonSchemaString())
                            } catch (e: ValidationException) {
                                isMatched = false
                            }
                        } catch (e: PathNotFoundException) {
                            // go on loop if path is not present
                        }
                    }
                }
                if (isMatched) {
                    // Add the matched result to the list
                    matches.add(
                        DescriptorMap(
                            format = "jwt_vp",
                            path = "$",
                            id = inputDescriptor.id ?: "",
                            pathNested = PathNested(
                                format = "jwt_vc",
                                id = generateUUID4(),
                                path = "$.vc[0]"
                            )
                        )
                    )
                    matchedCredentials.add(MatchedCredentials(vc = credential))
                }
            }
        }

        // Return the list of matched results
        return MatchCredentialResponse(
            matchedCredentials = matchedCredentials,
            descriptorMap = matches
        )
    }

    override fun normaliseCombineFormatForIssuanceToJsonString(
        claimJson: String,
        disclosureBase64s: List<String>
    ): String {
        TODO("Not yet implemented")
    }

    /**
     * This is a temporary solution to handle paths when the input descriptors
     *      didn't mention variations of the paths
     *      eg : $.vc.type and $.type
     * This had to be removed when everyone follows same structure
     */
    private fun buildVariationsOfJsonPath(pathList: List<String>?): List<String> {
        val newPathList: ArrayList<String> = ArrayList()

        for (path in pathList ?: ArrayList()) {
            newPathList.add(path)
            if (path.contains(".vc")) {
                if (pathList?.contains(path.replace(".vc", "")) != true)
                    newPathList.add(path.replace(".vc", ""))
            }

            if (!path.contains(".vc"))
                if (pathList?.contains("$.vc${path.replace("$", "")}") != true)
                    newPathList.add("$.vc${path.replace("$", "")}")
        }

        return newPathList
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
