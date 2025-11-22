package org.woowa.weathercodi.global.s3

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.woowa.weathercodi.global.config.AwsProperties
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.*

@Service
class ImageStorageService(
    private val s3Client: S3Client,
    private val awsProperties: AwsProperties
) {

    fun uploadClothesImage(deviceId: String, file: MultipartFile): String {
        val fileName = "${UUID.randomUUID()}.${file.originalFilename?.substringAfterLast('.') ?: "jpg"}"

        val key = "clothes/$deviceId/$fileName"

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(awsProperties.s3.bucket)
            .key(key)
            .contentType(file.contentType)
            .build()

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.bytes))

        return "https://${awsProperties.s3.bucket}.s3.amazonaws.com/$key"
    }

    fun uploadOutfitThumbnail(deviceId: String, file: MultipartFile): String {
        val fileName = "${UUID.randomUUID()}.${file.originalFilename?.substringAfterLast('.') ?: "jpg"}"

        val key = "outfits/$deviceId/$fileName"

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(awsProperties.s3.bucket)
            .key(key)
            .contentType(file.contentType)
            .build()

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.bytes))

        return "https://${awsProperties.s3.bucket}.s3.amazonaws.com/$key"
    }

    fun deleteFile(fileUrl: String) {
        val bucket = awsProperties.s3.bucket
        val key = fileUrl.substringAfter("$bucket.s3.amazonaws.com/")

        val deleteRequest = DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build()

        s3Client.deleteObject(deleteRequest)
    }
}