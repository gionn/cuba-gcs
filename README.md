# cuba-gcs

CUBA GCS add-on enables using Google Cloud Storage as a backend for file storage.

# Installation

The add-on can be added to your project in one of the ways described below.

In case you want to install the add-on by manual editing or by building from sources see the complete add-ons installation guide in CUBA Platform documentation [CUBA Platform documentation](https://doc.cuba-platform.com/manual-latest/manual.html#app_components_usage).

## From Github package

* Enable the repository for this package in your `build.gradle`:

```gradle
buildscript {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/gionn/cuba-gcs")
            credentials {
                username = rootProject.findProperty('githubUser') ?: System.getenv("GITHUB_PACKAGES_USERNAME")
                password = rootProject.findProperty('githubToken') ?: System.getenv("GITHUB_PACKAGES_PASSWORD")
            }
        }
    }
}
```

Github repository requires authentication even for public repositories.

* Create a [personal access token](https://github.com/settings/tokens) with `read:packages` scope

* Define it along with your Github username in `~/.gradle/gradle.properties`:

```properties
githubUser=
githubToken=
```

* Install the addon via CUBA > Marketplace > Install addon manually and provide the coordinates:

```
net.gionn.cubagcs:gcs-global:0.0.1-SNAPSHOT
```

# Configuration

 1. To add GCS support, you need to register the bean class in the `spring.xml` file in the `core` module:

 ```xml
     <bean name="cuba_FileStorage" class="net.gionn.cubagcs.core.GoogleCloudStorageFileStorage"/>
 ```
