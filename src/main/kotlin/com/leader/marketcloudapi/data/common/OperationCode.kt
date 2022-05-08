package com.leader.marketcloudapi.data.common

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

@Document(collection = "operation_codes")
class OperationCode {

    @Id
    lateinit var id: ObjectId
    lateinit var code: String
    lateinit var expires: Date
    lateinit var type: String
    lateinit var payload: org.bson.Document
}

interface OperationCodeRepository : MongoRepository<OperationCode, ObjectId> {

    fun existsByCode(code: String): Boolean

    fun deleteByCode(code: String)

    fun deleteByExpiresBefore(date: Date)

    fun deleteByType(type: String)

    fun findByCodeAndExpiresAfter(code: String, expires: Date): OperationCode?
}