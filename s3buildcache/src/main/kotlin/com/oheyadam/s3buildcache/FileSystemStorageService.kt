package com.oheyadam.s3buildcache

import java.io.File
import java.io.InputStream
import java.nio.file.Files

/**
 * An implementation of the [StorageService] that is backed by a file system.
 */
internal class FileSystemStorageService(
  override val region: String,
  override val bucketName: String,
  override val isPush: Boolean,
  override val isEnabled: Boolean
) : StorageService {

  private val location = Files.createTempDirectory("tmp$region$bucketName").toFile()

  override fun load(cacheKey: String): InputStream? {
    if (!isEnabled) {
      return null
    }

    val file = File(location, cacheKey)
    return if (file.exists() && file.isFile) {
      file.inputStream()
    } else {
      null
    }
  }

  override fun store(cacheKey: String, contents: ByteArray): Boolean {
    if (!isEnabled) {
      return false
    }

    if (!isPush) {
      return false
    }

    val file = File(location, cacheKey)
    val output = file.outputStream()
    output.use {
      output.write(contents)
    }
    return true
  }

  override fun delete(cacheKey: String): Boolean {
    if (!isEnabled) {
      return false
    }

    if (!isPush) {
      return false
    }

    val file = File(location, cacheKey)
    return file.delete()
  }

  override fun validateConfiguration() {
    // There is nothing to validate
  }

  override fun close() {
    location.deleteRecursively()
  }

  private fun File.deleteRecursively() {
    val files = listFiles() ?: return
    files.forEach { file ->
      if (file.isFile) {
        file.delete()
      }
      if (file.isDirectory) {
        file.deleteRecursively()
      }
    }
    delete()
  }
}
