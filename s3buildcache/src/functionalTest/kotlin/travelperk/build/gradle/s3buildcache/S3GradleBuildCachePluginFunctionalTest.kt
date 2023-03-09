package travelperk.build.gradle.s3buildcache

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import kotlin.test.Test

/**
 * A simple functional test for the 'travelperk.build.gradle.s3buildcache.S3BuildCache' plugin.
 */
class S3GradleBuildCachePluginFunctionalTest {

  @get:Rule val tempFolder = TemporaryFolder()

  private fun getProjectDir() = tempFolder.root
  private fun getBuildFile() = getProjectDir().resolve("build.gradle.kts")
  private fun getSettingsFile() = getProjectDir().resolve("settings.gradle.kts")

  @Test fun `can run tasks task`() {
    getSettingsFile().writeText(
      """
        import travelperk.build.gradle.s3buildcache.S3BuildCache
        
        plugins {
          id("travelperk.build.gradle.s3buildcache")
        }
        buildCache {
          remote(S3BuildCache::class) {
              region = "foo"
              bucketName = "bar"
          }
        }
        """.trimIndent()
    )
    getBuildFile().writeText("")

    // Run the build
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("tasks")
    runner.withProjectDir(getProjectDir())
    runner.build()
  }
}
