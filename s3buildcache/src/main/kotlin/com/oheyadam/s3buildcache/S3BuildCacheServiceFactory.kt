package com.oheyadam.s3buildcache

import org.gradle.caching.BuildCacheService
import org.gradle.caching.BuildCacheServiceFactory

/**
 * Factory used by Gradle to create S3BuildCache instances.
 */
class S3BuildCacheServiceFactory : BuildCacheServiceFactory<S3BuildCache> {

  override fun createBuildCacheService(
    buildCache: S3BuildCache,
    describer: BuildCacheServiceFactory.Describer
  ): BuildCacheService {
    describer
      .type("AWS-S3-backed")
      .config("region", buildCache.region)
      .config("bucketName", buildCache.bucketName)
      .config("reducedRedundancy", "${buildCache.reducedRedundancy}")
      .config("isPushSupported", "${buildCache.isPush}")
      .config("isEnabled", "${buildCache.isEnabled}")
      .config("sizeThreshold", "${buildCache.sizeThreshold}")
      .config("credentialsType", "${buildCache.credentials}")

    val service = S3BuildCacheService(
      region = buildCache.region,
      bucketName = buildCache.bucketName,
      isPush = buildCache.isPush,
      isEnabled = buildCache.isEnabled,
      reducedRedundancy = buildCache.reducedRedundancy,
      sizeThreshold = buildCache.sizeThreshold,
      credentials = buildCache.credentials
    )
    service.validateConfiguration()
    return service
  }
}
