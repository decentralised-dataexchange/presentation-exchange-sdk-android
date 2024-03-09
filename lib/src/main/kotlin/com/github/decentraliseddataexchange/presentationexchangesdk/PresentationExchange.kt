package com.github.decentraliseddataexchange.presentationexchangesdk

import com.jayway.jsonpath.JsonPath
import net.jimblackler.jsonschemafriend.Schema
import net.jimblackler.jsonschemafriend.SchemaStore
import net.jimblackler.jsonschemafriend.Validator


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

    fun validateJsonPath() {
        val json = """
            {
                "store": {
                    "book": [
                        {
                            "category": "reference",
                            "author": "Nigel Rees",
                            "title": "Sayings of the Century",
                            "price": 8.95
                        },
                        {
                            "category": "fiction",
                            "author": "Evelyn Waugh",
                            "title": "Sword of Honour",
                            "price": 12.99
                        },
                        {
                            "category": "fiction",
                            "author": "Herman Melville",
                            "title": "Moby Dick",
                            "isbn": "0-553-21311-3",
                            "price": 8.99
                        },
                        {
                            "category": "fiction",
                            "author": "J. R. R. Tolkien",
                            "title": "The Lord of the Rings",
                            "isbn": "0-395-19395-8",
                            "price": 22.99
                        }
                    ],
                    "bicycle": {
                        "color": "red",
                        "price": 19.95
                    }
                },
                "expensive": 10
            }
        """.trimIndent()
        val authors = JsonPath.read<List<String>>(json, "$.store.book[*].author")
        println(authors)
    }
}
