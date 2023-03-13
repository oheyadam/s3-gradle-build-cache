package com.oheyadam.s3buildcache

import org.gradle.caching.configuration.AbstractBuildCache

/**
 * Gradle Build Cache that uses AWS S3 buckets as a backing for load and store Gradle results.
 */
abstract class S3BuildCache : AbstractBuildCache() {

  /**
   * The AWS region the S3 bucket is located in.
   */
  lateinit var region: String

  /**
   * The name of the bucket that is used to store all the gradle cache entries.
   * This essentially becomes the root of all cache entries.
   */
  lateinit var bucketName: String

  /**
   * Whether to use reduced redundancy.
   * @see <a href="https://aws.amazon.com/s3/reduced-redundancy/">Reduced Redundancy</a>
   * */
  var reducedRedundancy: Boolean = true

  /**
   * The size limit on blobs that we can upload/download
   * */
  var sizeThreshold: Long = 50 * 1024 * 1024

  /**
   * The type of credentials to use to connect to AWS.
   */
  var credentials: S3Credentials = DefaultS3Credentials
}
