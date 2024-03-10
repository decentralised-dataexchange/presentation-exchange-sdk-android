package com.github.decentraliseddataexchange.presentationexchangesdk

fun main() {
    val pex = PresentationExchange()
    val inputDescriptor = """
        {
          "id": "9a18d1b5-13ac-4fbc-8c12-d5916740ce1d",
          "constraints": {
            "fields": [
              {
                "path": [
                  "${'$'}.type"
                ],
                "filter": {
                  "type": "array",
                  "contains": {
                    "const": "Passport"
                  }
                }
              }
            ]
          }
        }""".trimIndent()
    val credentialsList = listOf(
        """
        {
          "type": [
            "Passport"
          ]
        }
    """.trimIndent(),
        """
        {
          "type": [
            "Passport"
          ]
        }
    """.trimIndent()
    )
    val matches = pex.matchCredentials(inputDescriptor, credentialsList)
    println(matches)
}