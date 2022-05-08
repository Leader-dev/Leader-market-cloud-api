package com.leader.marketcloudapi.util.component

import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*

@Component
class RandomUtil {

    private val random = SecureRandom()

    private val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcsdefghijklmnopqrstuvwxyz0123456789"

    fun nextDouble() = random.nextDouble()

    fun nextInt(bound: Int)= random.nextInt(bound)

    fun nextUUID() = UUID.randomUUID()!!

    fun nextSalt(length: Int): String {
        val saltBuilder = StringBuilder(length)
        for (i in 0 until length) {
            val randomIndex = nextInt(charset.length)
            val ch = charset[randomIndex]
            saltBuilder.append(ch)
        }
        return saltBuilder.toString()
    }

    fun nextSalt(length: Int, criteria: (String) -> Boolean): String {
        var salt: String
        do {
            salt = nextSalt(length)
        } while (!criteria(salt))
        return salt
    }

    fun nextAuthCode(length: Int): String {
        val randomNumber = nextDouble()
        return randomNumber.toString().substring(2, 2 + length)
    }

    fun nextNumberID(length: Int, criteria: (String) -> Boolean): String {
        var generated: String
        do {  // ensure that first digit is not 0
            val randomNumber = nextDouble()
            generated = randomNumber.toString().substring(2, 2 + length)
        } while (generated.startsWith("0") || !criteria(generated))
        return generated
    }
}