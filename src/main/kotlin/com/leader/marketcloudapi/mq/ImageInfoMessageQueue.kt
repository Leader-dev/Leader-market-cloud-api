package com.leader.marketcloudapi.mq

import com.leader.marketcloudapi.util.InternalErrorException
import org.bson.Document
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.Queue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component


@Component
class ImageInfoMessageQueue @Autowired constructor(
    private val amqpTemplate: AmqpTemplate
) {
    companion object {
        private const val IMAGE_UPLOADED = "images-uploaded"
    }

    @Bean
    fun imageUploadedQueue() = Queue(IMAGE_UPLOADED)

    fun assertImagesUploaded(imageUrls: List<String>) {
        val reply = amqpTemplate.convertSendAndReceive(IMAGE_UPLOADED, Document("operation", "assert").append("imageUrls", imageUrls))
                as? Boolean ?: throw IllegalStateException("Failed to receive response from image-service.")
        if (!reply) {
            throw InternalErrorException("Failed to assert images uploaded.")
        }
    }

    fun confirmImagesUploaded(imageUrls: List<String>) {
        val reply = amqpTemplate.convertSendAndReceive(IMAGE_UPLOADED, Document("operation", "confirm").append("imageUrls", imageUrls))
                as? Boolean ?: throw IllegalStateException("Failed to receive response from image-service.")
        if (!reply) {
            throw InternalErrorException("Failed to confirm images uploaded.")
        }
    }
}