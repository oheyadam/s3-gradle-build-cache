# AWS S3 backed Gradle Remote Cache

An implementation of the Gradle Remote Cache that's backed by AWS S3 buckets.

## Using the plugin

In your `settings.gradle(.kts)` file add the following

```kotlin
plugins {
  id("io.github.oheyadam.s3buildcache") version "1.0-beta02"
}

import com.oheyadam.s3buildcache.S3BuildCache
import com.oheyadam.s3buildcache.S3BuildCacheServiceFactory
import com.oheyadam.s3buildcache.ExportedS3Credentials

buildCache {
  registerBuildCacheService(S3BuildCache::class, S3BuildCacheServiceFactory::class)
  remote(S3BuildCache::class) {
    region = "s3-bucket-region"
    bucketName = "s3-bucket-name"
    credentials = ExportedS3Credentials("your-aws-access-key-id", "your-aws-secret-key")
    isPush = System.getenv().containsKey("CI")
  }
}
```

- `region` and `bucketName` are required.
- `sizeThreshold` defaults to 50 MB, but can be changed to control the size limit of the blobs you want to upload/download.
- `credentials` defaults to `DefaultS3Credentials`, but can also be set to `ProfileS3Credentials`, `ExportedS3Credentials`, or `SpecificCredentialsProvider`.
- `isPush` defaults to `false`.

---

If you are using Groovy, then you should do the following:

```groovy
plugins {
  id("io.github.oheyadam.s3buildcache") version "1.0-beta02"
}

import com.oheyadam.s3buildcache.ExportedS3Credentials
import com.oheyadam.s3buildcache.S3BuildCache
import com.oheyadam.s3buildcache.S3BuildCacheServiceFactory

buildCache {
  registerBuildCacheService(S3BuildCache, S3BuildCacheServiceFactory)
  remote(S3BuildCache) {
    region = "s3-bucket-region"
    bucketName = "s3-bucket-name"
    credentials = new ExportedS3Credentials("your-aws-access-key-id", "your-aws-secret-key")
    push = System.getenv().containsKey("CI")
  }
}
```
