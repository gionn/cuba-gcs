# cuba-gcs

CUBA GCS add-on enables using Google Cloud Storage as a backend for file storage.

# Installation

The add-on can be added to your project in one of the ways described below:

* From prebuilt package hosted on Github
* From sources

In case you want to install the add-on by manual editing or by building from sources see the complete add-ons
installation guide in CUBA Platform documentation
[CUBA Platform documentation](https://doc.cuba-platform.com/manual-latest/manual.html#app_components_usage).

## From prebuilt package hosted on Github

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

Github repository requires authentication even if it is public.

* Create a [personal access token](https://github.com/settings/tokens) with `read:packages` scope

* Define it along with your Github username in `~/.gradle/gradle.properties`:

```properties
githubUser=
githubToken=
```

## From sources

Clone repository locally and install it via:

```bash
./gradlew install
```

or CUBA -> Advanced -> Install App Component.

Add the local repository to your project `build.gradle`:

```gradle
buildscript {
    repositories {
        mavenLocal()
    }
}
```

## Install addon

Finally, install the addon in your project via `CUBA > Marketplace > Install addon manually` and provide the coordinates:

```
net.gionn.cubagcs:gcs-global:0.0.1-SNAPSHOT
```

# Configuration

 To add GCS support, you need to register the bean class in the `spring.xml` file in the `core` module:

 ```xml
     <bean name="cuba_FileStorage" class="net.gionn.cubagcs.core.GoogleCloudStorageFileStorage"/>
 ```

Configure authentication via a dedicated Service Account, providing the path to json key via
`GOOGLE_APPLICATION_CREDENTIALS` environment variable. More information on
[Authenticating as a service account](https://cloud.google.com/docs/authentication/production).

## Properties

### cuba.gcs.bucket

The Google Cloud Storage bucket where to store files

### cuba.gcs.projectId

The Google Cloud project id where the bucket is present.

## Known issues

Fix incompatibility with guava version after enabling the addon:

```gradle
configure(globalModule) {
    dependencies {
        // required for net.gionn.cubagcs to pull the correct guava flavour
        constraints {
            compile 'com.google.guava:guava:30.1-jre'
        }
    }
```
