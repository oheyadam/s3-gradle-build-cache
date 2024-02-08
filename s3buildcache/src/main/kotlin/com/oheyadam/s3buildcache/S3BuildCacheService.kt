package com.oheyadam.s3buildcache

import org.gradle.api.logging.Logging
import org.gradle.caching.BuildCacheEntryReader
import org.gradle.caching.BuildCacheEntryWriter
import org.gradle.caching.BuildCacheKey
import org.gradle.caching.BuildCacheService
import java.io.ByteArrayOutputStream

/**
 * The service that responds to Gradle's request to load and store results for a given
 * [BuildCacheKey].
 *
 * @param region The AWS region the S3 bucket is located in.
 * @param bucketName The name of the bucket that is used to store all the gradle cache entries.
 * @param reducedRedundancy Whether to use reduced redundancy.
 * This essentially becomes the root of all cache entries.
 */
class S3BuildCacheService(
  credentials: S3Credentials,
  region: String,
  bucketName: String,
  isPush: Boolean,
  isEnabled: Boolean,
  reducedRedundancy: Boolean,
  sizeThreshold: Long,
  inTestMode: Boolean = false
) : BuildCacheService {

  private val storageService = if (inTestMode) {
    FileSystemStorageService(
      region = region,
      bucketName = bucketName,
      isPush = isPush,
      isEnabled = isEnabled,
      sizeThreshold = sizeThreshold
    )
  } else {
    S3StorageService(
      region = region,
      bucketName = bucketName,
      isPush = isPush,
      isEnabled = isEnabled,
      sizeThreshold = sizeThreshold,
      reducedRedundancy = reducedRedundancy,
      credentials = credentials
    )
  }

  override fun load(key: BuildCacheKey, reader: BuildCacheEntryReader): Boolean {
    logger.info("Loading ${key.blobKey()}")
    val cacheKey = key.blobKey()
    val input = storageService.load(cacheKey) ?: return false
    reader.readFrom(input)
    return true
  }

  override fun store(key: BuildCacheKey, writer: BuildCacheEntryWriter) {
    if (writer.size == 0L) return // do not store empty entries into the cache
    logger.info("Storing ${key.blobKey()}")
    val cacheKey = key.blobKey()
    val output = ByteArrayOutputStream()
    output.use {
      writer.writeTo(output)
    }
    storageService.store(cacheKey, output.toByteArray())
  }

  override fun close() {
    storageService.close()
  }

  fun validateConfiguration() {
    storageService.validateConfiguration()
  }

  companion object {

    private val logger by lazy {
      Logging.getLogger("AwsS3BuildCacheService")
    }
    private val SLASHES = """"/+""".toRegex()

    private fun BuildCacheKey.blobKey(): String {
      // Slashes are special when it comes to cache keys.
      // Under the hood, they are treated as a "folder/file" as long as there is
      // a single `/`.
      return hashCode.replace(SLASHES, "/")
    }
  }
}
