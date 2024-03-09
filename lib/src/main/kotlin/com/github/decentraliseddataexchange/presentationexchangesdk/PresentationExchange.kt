package com.github.decentraliseddataexchange.presentationexchangesdk

import net.jimblackler.jsonschemafriend.Schema;
import net.jimblackler.jsonschemafriend.SchemaStore;
import net.jimblackler.jsonschemafriend.Validator;

class PresentationExchange() {
    private var schemaString: String? = null

    fun setSchema(schema: String) {
        this.schemaString = schema
    }

    fun validateJsonSchema(input: String) {
        if (schemaString == null) {
            throw IllegalStateException("Schema not set. Call setSchema before calling validateJsonSchema.")
        }

        // Initialize a SchemaStore.
        val schemaStore = SchemaStore()
        // Load the schema.
        val schema: Schema = schemaStore.loadSchemaJson(schemaString!!)
        // Create a validator.
        val validator = Validator()
        // Validate JSON based on the input, if failed will throw ValidationException
        validator.validateJson(schema, input)
    }
}
