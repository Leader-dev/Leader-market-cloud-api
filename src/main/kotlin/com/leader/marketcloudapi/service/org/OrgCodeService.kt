package com.leader.marketcloudapi.service.org

import com.leader.marketcloudapi.service.common.OperationCodeService
import com.leader.marketcloudapi.util.InternalErrorException
import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

class OrgCodeInfo(
    val type: String,
    val orgId: ObjectId,
    val oldAdminMemberId: ObjectId?
)

@Service
class OrgCodeService @Autowired constructor(
    private val operationCodeService: OperationCodeService
) {

    companion object {
        const val JOIN_CODE_TYPE = "join"
        const val TRANSFER_ADMIN_CODE_TYPE = "transfer_admin"

        private const val ORG_ID_KEY = "org_id"
        private const val OLD_ADMIN_MEMBER_ID_KEY = "old_admin_member_id"
    }

    fun generateJoinCode(orgId: ObjectId, expiresIn: Long): String {
        val payload = Document(ORG_ID_KEY, orgId)
        return operationCodeService.generateOperationCode(JOIN_CODE_TYPE, payload, expiresIn)
    }

    fun generateTransferAdminCode(orgId: ObjectId, oldAdminMemberId: ObjectId, expiresIn: Long): String {
        val payload = Document(ORG_ID_KEY, orgId).append(OLD_ADMIN_MEMBER_ID_KEY, oldAdminMemberId)
        return operationCodeService.generateOperationCode(TRANSFER_ADMIN_CODE_TYPE, payload, expiresIn)
    }

    fun getCodeInfo(code: String): OrgCodeInfo {
        val info = operationCodeService.getOperationCodeInfo(code)
            ?: throw InternalErrorException("Invalid code")
        return OrgCodeInfo(
            info.type,
            info.payload[ORG_ID_KEY] as ObjectId,
            info.payload[OLD_ADMIN_MEMBER_ID_KEY] as? ObjectId
        )
    }

    fun deleteCode(code: String) {
        operationCodeService.deleteOperationCode(code)
    }
}