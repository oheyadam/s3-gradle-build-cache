package travelperk.build.gradle.s3buildcache

import org.junit.Test

/**
 * This test needs to be configured with the correct values for REGION, BUCKET_NAME,
 * and AWS credentials, therefore, it won't pass on CI as is.
 * */
class S3StorageServiceTest {

  @Test fun testStoreBlob() {
    val storageService = S3StorageService(
      region = REGION,
      bucketName = BUCKET_NAME,
      credentials = DefaultS3Credentials,
      isPush = true,
      isEnabled = true,
      reducedRedundancy = true
    )
    storageService.use {
      val cacheKey = "test-store.txt"
      val contents = "The quick brown fox jumped over the lazy dog"
      val result = storageService.store(cacheKey, contents.toByteArray(Charsets.UTF_8))
      assert(result)
      storageService.delete(cacheKey)
    }
  }

  @Test fun testLoadBlob() {
    val storageService = S3StorageService(
      region = REGION,
      bucketName = BUCKET_NAME,
      credentials = DefaultS3Credentials,
      isPush = true,
      isEnabled = true,
      reducedRedundancy = true
    )
    storageService.use {
      val cacheKey = "test-load.txt"
      val contents = "The quick brown fox jumped over the lazy dog"
      val bytes = contents.toByteArray(Charsets.UTF_8)
      assert(storageService.store(cacheKey, bytes))
      val input = storageService.load(cacheKey)!!
      val result = String(input.readAllBytes(), Charsets.UTF_8)
      assert(result == contents)
      storageService.delete(cacheKey)
    }
  }

  @Test fun testStoreBlob_noPushSupport() {
    val storageService = S3StorageService(
      region = REGION,
      bucketName = BUCKET_NAME,
      credentials = DefaultS3Credentials,
      isPush = false,
      isEnabled = true,
      reducedRedundancy = true
    )
    storageService.use {
      val cacheKey = "test-store-no-push.txt"
      val contents = "The quick brown fox jumped over the lazy dog"
      val result = storageService.store(cacheKey, contents.toByteArray(Charsets.UTF_8))
      assert(!result)
    }
  }

  @Test fun testLoadBlob_noPushSupport() {
    val storageService = S3StorageService(
      region = REGION,
      bucketName = BUCKET_NAME,
      credentials = DefaultS3Credentials,
      isPush = true,
      isEnabled = true,
      reducedRedundancy = true
    )
    val readOnlyStorageService = S3StorageService(
      region = REGION,
      bucketName = BUCKET_NAME,
      credentials = DefaultS3Credentials,
      isPush = false,
      isEnabled = true,
      reducedRedundancy = true
    )
    storageService.use {
      readOnlyStorageService.use {
        val cacheKey = "test-load-no-push.txt"
        val contents = "The quick brown fox jumped over the lazy dog"
        val bytes = contents.toByteArray(Charsets.UTF_8)
        assert(storageService.store(cacheKey, bytes))
        val input = readOnlyStorageService.load(cacheKey)!!
        val result = String(input.readAllBytes(), Charsets.UTF_8)
        assert(result == contents)
        storageService.delete(cacheKey)
      }
    }
  }

  @Test fun testLoadBlob_disabled() {
    val storageService = S3StorageService(
      region = REGION,
      bucketName = BUCKET_NAME,
      credentials = DefaultS3Credentials,
      isPush = true,
      isEnabled = false,
      reducedRedundancy = true
    )
    storageService.use {
      val cacheKey = "test-store-disabled.txt"
      val contents = "The quick brown fox jumped over the lazy dog"
      val result = storageService.store(cacheKey, contents.toByteArray(Charsets.UTF_8))
      assert(!result)
    }
  }

  companion object {

    private const val REGION = "your-bucket-region"
    private const val BUCKET_NAME = "your-bucket-name"
  }
}
