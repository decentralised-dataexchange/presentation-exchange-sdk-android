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
              },
              {
                "path": [
                  "${'$'}.address.city"
                ],
                "filter": {
                  "type": "string",
                    "pattern": "EKM"
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
          ],
          "name": "John",
          "dob": "14-Mar-70",
          "address": {
            "city": "EKM",
            "state": "Kerala"
          }
        }
    """.trimIndent(),
        """
        {
          "type": [
            "Passport"
          ],
          "name": "John",
          "dob": "14-Mar-70",
          "address": {
            "city": "EKM",
            "state": "Kerala"
          }
        }
    """.trimIndent(),
        """
        {
          "type": [
            "Passport"
          ],
          "name": "John",
          "dob": "14-Mar-70",
          "address": {
            "city": "TVM",
            "state": "Kerala"
          }
        }
    """.trimIndent(),
        """
        {
          "type": [
            "Ess"
          ],
          "name": "John",
          "dob": "14-Mar-70",
          "address": {
            "city": "EKM",
            "state": "Kerala"
          }
        }
    """.trimIndent()
    )
    val matches = pex.matchCredentials(inputDescriptor, credentialsList)
    println(matches)
}