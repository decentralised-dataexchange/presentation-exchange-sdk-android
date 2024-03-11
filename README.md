<h1 align="center">
    Presentation Exchange SDK (Android)
</h1>

<p align="center">
    <a href="/../../commits/" title="Last Commit"><img src="https://img.shields.io/github/last-commit/decentralised-dataexchange/presentation-exchange-sdk-android?style=flat"></a>
    <a href="/../../issues" title="Open Issues"><img src="https://img.shields.io/github/issues/decentralised-dataexchange/presentation-exchange-sdk-android?style=flat"></a>
    <a href="./LICENSE" title="License"><img src="https://img.shields.io/badge/License-Apache%202.0-yellowgreen?style=flat"></a>
</p>

<p align="center">
  <a href="#about">About</a> •
  <a href="#contributing">Contributing</a> •
  <a href="#licensing">Licensing</a>
</p>


## About

This repository hosts source code for presentation exchange SDK.

## Usage

1. Add the JitPack repository to your build file

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

2. Add the dependency

```kotlin
implementation 'com.github.decentralised-dataexchange:presentation-exchange-sdk-android:<tag>'
```

Note: In order to fix `slf4j` `java.lang.NullPointerException` found in versions >= 2.0.10, do override the transitive dependency by specifying the below:

```kotlin
implementation('org.slf4j:slf4j-api') {
    version {
        strictly '2.0.9'
    }
}
```

3. Example usage of the SDK:

```kotlin
import com.github.decentraliseddataexchange.presentationexchangesdk.PresentationExchange
import com.github.decentraliseddataexchange.presentationexchangesdk.models.MatchedCredential
import com.google.gson.Gson


fun main() {
    val pex = PresentationExchange()
    val inputDescriptor =
        """{"id":"9a18d1b5-13ac-4fbc-8c12-d5916740ce1d","constraints":{"fields":[{"path":["${'$'}.address.city"]}]}}"""
    val credentialsList = listOf(
        """{"type":["Passport"],"name":"John","dob":"14-Mar-70","address":{"city":"Stockholm","state":"Stockholm"}}""",
    )
    val matches: List<MatchedCredential> = pex.matchCredentials(inputDescriptor, credentialsList)
    println(Gson().toJson(matches))
}
```

Will output:

```json
[
  {
    "index": 0,
    "fields": [
      {
        "index": 0,
        "path": {
          "path": "$.address.city",
          "index": 0,
          "value": "Stockholm"
        }
      }
    ]
  }
]
```

## Contributing

Feel free to improve the plugin and send us a pull request. If you find any problems, please create an issue in this repo.

## Licensing
Copyright (c) 2023-25 LCubed AB (iGrant.io), Sweden

Licensed under the Apache 2.0 License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the LICENSE for the specific language governing permissions and limitations under the License.