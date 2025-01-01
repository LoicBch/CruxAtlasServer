package com.example.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.security.Key
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

fun Route.models() {
    route("/models") {

        get("/{id}") {
            val modelId =
                call.parameters["id"]?.toIntOrNull()?.let { String.format("%03d", it) } ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid ID"
                )

            val modelFile = File("/app/resources/static/models/$modelId/$modelId.glb")
            val routesFile = File(
                "/app/resources/static/models/$modelId/routes.json"
            )
            val infoFile = File("/app/resources/static/models/$modelId/info.json")

            if (!modelFile.exists() || !routesFile.exists() || !infoFile.exists()) {
                return@get call.respond(HttpStatusCode.NotFound, "One or more files not found for model ID: $modelId")
            }

            println("all files are found")

            val encryptedFiles = mutableListOf<File>()
            val filesToEncrypt = listOf(modelFile, routesFile, infoFile)

            val zipFile = File.createTempFile("model_$modelId", ".zip")
            println("temp zip file created")
            var encryptedZipFile: File? = null
            try {

                filesToEncrypt.forEach { file ->
                    println("encrypting file: $file")
                    val encryptedFile = File.createTempFile(file.nameWithoutExtension, ".enc")
                    file.encryptFileWithAES(encryptedFile, secretKeyFiles)
                    encryptedFiles.add(encryptedFile)
                }

                println("creating zip file")
                ZipOutputStream(zipFile.outputStream()).use { zipOut ->
                    encryptedFiles.forEach { file ->
                        zipOut.putNextEntry(ZipEntry(file.name))
                        file.inputStream().use { it.copyTo(zipOut) }
                        zipOut.closeEntry()
                    }
                }

                println("zip file created")

                encryptedZipFile = File.createTempFile("encrypted_model_$modelId", ".zip")

                println("encrypting zip file")
                zipFile.encryptFileWithAES(encryptedZipFile, secretKey)

                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(
                        ContentDisposition.Parameters.FileName,
                        "model_$modelId.zip"
                    ).toString()
                )
                call.respondFile(encryptedZipFile)
            } finally {
                zipFile.delete()
                encryptedZipFile?.delete()
                encryptedFiles.forEach { it.delete() }
                println("deleted temp files")
            }
        }
    }
}

fun File.encryptFileWithAES(outputFile: File, secretKey: String) {
    val key = getSecretKeyFromHex(secretKey)
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.ENCRYPT_MODE, key)

    this.inputStream().use { input ->
        outputFile.outputStream().use { output ->
            val encryptedBytes = cipher.doFinal(input.readBytes())
            output.write(encryptedBytes)
        }
    }
}

fun generateSecretKey(): Key {
    val keyGenerator = KeyGenerator.getInstance("AES")
    keyGenerator.init(256)  // Taille de la clé (128, 192 ou 256 bits)
    return keyGenerator.generateKey()
}

fun getSecretKeyFromHex(hexKey: String): Key {
    val keyBytes = hexKey.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    return SecretKeySpec(keyBytes, "AES")
}

val secretKey = "43a114e32ae45d9a71c0f0f4a40078cb0ad01dfa9330eb824577791409e48043"
val secretKeyFiles = "c4dc317d25673abd05bac162acc539dcb9fcaa8d1297e97a0c02dfd9b8c2909a"