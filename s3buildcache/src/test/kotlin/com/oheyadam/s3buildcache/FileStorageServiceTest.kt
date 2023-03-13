package com.oheyadam.s3buildcache

import org.junit.Test

class FileStorageServiceTest {

  @Test fun testStoreBlob() {
    val storageService = FileSystemStorageService(
      region = REGION,
      bucketName = BUCKET_NAME,
      isPush = true,
      isEnabled = true,
      sizeThreshold = SIZE_THRESHOLD
    )
    storageService.use {
      val cacheKey = "test-store.txt"
      val contents = "The quick brown fox jumps over the lazy dog"
      val result = storageService.store(cacheKey, contents.toByteArray(Charsets.UTF_8))
      assert(result)
    }
  }

  @Test fun testLoadBlob() {
    val storageService = FileSystemStorageService(
      region = REGION,
      bucketName = BUCKET_NAME,
      isPush = true,
      isEnabled = true,
      sizeThreshold = SIZE_THRESHOLD
    )
    storageService.use {
      val cacheKey = "test-load.txt"
      val contents = "The quick brown fox jumps over the lazy dog"
      val bytes = contents.toByteArray(Charsets.UTF_8)
      storageService.store(cacheKey, bytes)
      val input = storageService.load(cacheKey)!!
      val result = String(input.readAllBytes(), Charsets.UTF_8)
      assert(result == contents)
    }
  }

  @Test fun testLoadBlobTooLarge() {
    val storageService = FileSystemStorageService(
      region = REGION,
      bucketName = BUCKET_NAME,
      isPush = true,
      isEnabled = true,
      sizeThreshold = 1
    )
    storageService.use {
      val cacheKey = "test-load.txt"
      val contents = "The quick brown fox jumps over the lazy dog"
      val bytes = contents.toByteArray(Charsets.UTF_8)
      storageService.store(cacheKey, bytes)
      val input = storageService.load(cacheKey)
      assert(input == null)
    }
  }

  @Test fun testStoreBlob_noPushSupport() {
    val storageService = FileSystemStorageService(
      region = REGION,
      bucketName = BUCKET_NAME,
      isPush = false,
      isEnabled = true,
      sizeThreshold = SIZE_THRESHOLD
    )
    storageService.use {
      val cacheKey = "test-store-no-push.txt"
      val contents = "The quick brown fox jumps over the lazy dog"
      val result = storageService.store(cacheKey, contents.toByteArray(Charsets.UTF_8))
      assert(!result)
    }
  }

  @Test fun testStoreBlob_disabled() {
    val storageService = FileSystemStorageService(
      region = REGION,
      bucketName = BUCKET_NAME,
      isPush = true,
      isEnabled = false,
      sizeThreshold = SIZE_THRESHOLD
    )
    storageService.use {
      val cacheKey = "test-store-disabled.txt"
      val contents = "The quick brown fox jumps over the lazy dog"
      val result = storageService.store(cacheKey, contents.toByteArray(Charsets.UTF_8))
      assert(!result)
    }
  }

  companion object {

    private const val REGION = "region"
    private const val BUCKET_NAME = "bucket-name"
    private const val SIZE_THRESHOLD = 50 * 1024 * 1024L
  }
}
