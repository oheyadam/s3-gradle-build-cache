package com.oheyadam.s3buildcache

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

/**
 * A Gradle settings plugin that registers [S3BuildCache]
 */
class S3GradleBuildCachePlugin : Plugin<Settings> {
  override fun apply(settings: Settings) {
    settings.buildCache.registerBuildCacheService(
      S3BuildCache::class.java,
      S3BuildCacheServiceFactory::class.java
    )
  }
}
