plugins {
  id("com.gradle.plugin-publish") version "1.1.0"
  id("org.jetbrains.kotlin.jvm") version "1.8.10"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation(platform("software.amazon.awssdk:bom:2.20.18"))
  implementation("software.amazon.awssdk:s3")
  implementation("software.amazon.awssdk:sso")
}

pluginBundle {
  website = "https://github.com/travelperk/s3-gradle-build-cache"
  vcsUrl = "https://github.com/travelperk/s3-gradle-build-cache"
  tags = listOf("buildcache", "s3", "caching")
}

gradlePlugin {
  plugins {
    create("s3buildcache") {
      id = "travelperk.build.gradle.s3buildcache"
      displayName = "Gradle AWS S3 Build Cache Plugin"
      description = "Gradle remote cache backed by AWS S3"
      implementationClass = "travelperk.build.gradle.s3buildcache.S3GradleBuildCachePlugin"
    }
  }
}

group = "travelperk.build.gradle.s3buildcache"
version = "1.0-beta01"

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
  withJavadocJar()
  withSourcesJar()
}

testing {
  suites {
    // Configure the built-in test suite
    val test by getting(JvmTestSuite::class) {
      // Use Kotlin Test Framework
      useKotlinTest()
    }

    // Create a new test suite
    val functionalTest by registering(JvmTestSuite::class) {
      // Use Kotlin Test Framework
      useKotlinTest()

      dependencies {
        // functionalTest test suite depends on the production code in tests
        implementation(project)
      }

      targets {
        all {
          // This test suite should run after the built-in test suite has run its tests
          testTask.configure { shouldRunAfter(test) }
        }
      }
    }
  }
}

gradlePlugin.testSourceSets(sourceSets["functionalTest"])

tasks.named<Task>("check") {
  // Include functionalTest as part of the check lifecycle
  dependsOn(testing.suites.named("functionalTest"))
}
