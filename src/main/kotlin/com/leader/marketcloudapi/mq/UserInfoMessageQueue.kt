package com.leader.marketcloudapi.mq

import com.leader.marketcloudapi.service.user.UserInfoService
import org.bson.Document
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class UserInfoMessageQueue @Autowired constructor(
    private val userInfoService: UserInfoService
) {

    companion object {
        private const val USER_NICKNAME_UPDATED = "user-nickname-updated"
    }

    @Bean
    fun userNameUpdatedQueue(): Queue {
        return Queue(USER_NICKNAME_UPDATED)
    }

    @RabbitListener(queues = [USER_NICKNAME_UPDATED])
    fun listenUserNicknameUpdated(message: Document) {
        userInfoService.updateUserNickname(message.getObjectId("userId"), message.getString("nickname"))
    }
}
