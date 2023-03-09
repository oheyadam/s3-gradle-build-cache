package travelperk.build.gradle.s3buildcache

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider

/**
 * [DefaultS3Credentials] or [ExportedS3Credentials] to use to authenticate to AWS.
 */
sealed interface S3Credentials

/**
 * Use DefaultCredentialsProvider to authenticate to AWS.
 */
object DefaultS3Credentials : S3Credentials

/**
 * Use a specific credentials provider
 * @see <a href="https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html">Using credentials</a>
 * */
class SpecificCredentialsProvider(val provider: AwsCredentialsProvider) : S3Credentials

/**
 * Use provided keys to authenticate to AWS.
 */
class ExportedS3Credentials(
  val awsAccessKeyId: String,
  val awsSecretKey: String
) : S3Credentials
