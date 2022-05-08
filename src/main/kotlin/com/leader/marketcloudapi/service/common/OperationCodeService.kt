package com.leader.marketcloudapi.service.common

import com.leader.marketcloudapi.data.common.OperationCode
import com.leader.marketcloudapi.data.common.OperationCodeRepository
import com.leader.marketcloudapi.util.component.DateUtil
import com.leader.marketcloudapi.util.component.RandomUtil
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class OperationCodeService @Autowired constructor(
    private val operationCodeRepository: OperationCodeRepository,
    private val randomUtil: RandomUtil,
    private val dateUtil: DateUtil
) {

    private fun cleanup() {
        operationCodeRepository.deleteByExpiresBefore(dateUtil.getCurrentDate())
    }

    private fun insertOperationCode(code: String, type: String, expires: Date, payload: Document): OperationCode {
        val operationCode = OperationCode()
        operationCode.code = code
        operationCode.expires = expires
        operationCode.type = type
        operationCode.payload = payload
        return operationCodeRepository.save(operationCode)
    }

    fun generateOperationCode(type: String, payload: Document, expireIn: Long): String {
        cleanup()
        operationCodeRepository.deleteByType(type)
        val code = randomUtil.nextSalt(16) { !operationCodeRepository.existsByCode(it) }
        return insertOperationCode(code, type, dateUtil.getCurrentDatePlus(expireIn), payload).code
    }

    data class OperationCodeInfo(val type: String, val payload: Document)

    fun getOperationCodeInfo(code: String): OperationCodeInfo? {
        val operationCode = operationCodeRepository.findByCodeAndExpiresAfter(code, dateUtil.getCurrentDate())
            ?: return null
        return OperationCodeInfo(operationCode.type, operationCode.payload)
    }

    fun deleteOperationCode(code: String) {
        operationCodeRepository.deleteByCode(code)
    }
}