package com.leader.marketcloudapi.service.user

import com.leader.marketcloudapi.data.user.User
import com.leader.marketcloudapi.data.user.UserRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserInfoService @Autowired constructor(
    private val userRepository: UserRepository
) {

    fun updateUserNickname(userId: ObjectId, nickname: String) {
        val user = userRepository.findById(userId).orElseGet { User() }
        user.id = userId
        user.nickname = nickname
        userRepository.save(user)
    }
}