package travelperk.build.gradle.s3buildcache

import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

/**
 * An input stream that keeps track of the file handles, so they can be cleaned up when the
 * [FileHandleInputStream] is closed.
 */
internal class FileHandleInputStream(private val file: File) : InputStream() {

  private val inputStream by lazy {
    file.inputStream()
  }

  override fun read(): Int {
    return inputStream.read()
  }

  override fun close() {
    super.close()
    try {
      inputStream.close()
    } finally {
      check(file.delete()) {
        "Unable to delete $file"
      }
    }
  }

  companion object {

    fun create(): Path {
      val timestamp = System.nanoTime()
      return Files.createTempFile("gradleCache", "$timestamp")
    }

    fun Path.handleInputStream(): InputStream {
      return FileHandleInputStream(this.toFile())
    }
  }
}
