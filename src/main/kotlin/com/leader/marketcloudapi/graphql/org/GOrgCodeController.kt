package com.leader.marketcloudapi.graphql.org

import com.leader.marketcloudapi.service.context.ContextService
import com.leader.marketcloudapi.service.org.OrgCodeInfo
import com.leader.marketcloudapi.service.org.OrgCodeService
import com.leader.marketcloudapi.service.org.OrgMemberService
import com.leader.marketcloudapi.util.InternalErrorException
import com.leader.marketcloudapi.util.success
import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class GOrgCodeController @Autowired constructor(
    private val orgMemberService: OrgMemberService,
    private val orgCodeService: OrgCodeService,
    private val contextService: ContextService
) {

    @SchemaMapping(typeName = "OrgMutation")
    fun getJoinCode(@Argument orgId: ObjectId, @Argument expiresIn: Long): String {
        val memberId = contextService.memberId
        orgMemberService.assertIsAdmin(memberId)
        return orgCodeService.generateJoinCode(orgId, expiresIn)
    }

    @SchemaMapping(typeName = "OrgMutation")
    fun getAdminTransferCode(@Argument orgId: ObjectId, @Argument expiresIn: Long): String {
        val memberId = contextService.memberId
        orgMemberService.assertIsAdmin(memberId)
        return orgCodeService.generateTransferAdminCode(orgId, memberId, expiresIn)
    }

    @SchemaMapping(typeName = "OrgMutation")
    fun getCodeInfo(@Argument opcode: String): OrgCodeInfo = orgCodeService.getCodeInfo(opcode)

    @SchemaMapping(typeName = "OrgMutation")
    fun joinOrganization(@Argument opcode: String): Boolean {
        val agentId = contextService.agentId

        val info = orgCodeService.getCodeInfo(opcode)
        if (info.type != OrgCodeService.JOIN_CODE_TYPE) {
            throw InternalErrorException("Code type is not join.")
        }
        orgMemberService.addMember(info.orgId, agentId)
        orgCodeService.deleteCode(opcode)

        return true
    }

    @SchemaMapping(typeName = "OrgMutation")
    fun claimOrganization(@Argument opcode: String): Document {
        val agentId = contextService.agentId

        val info = orgCodeService.getCodeInfo(opcode)
        if (info.type != OrgCodeService.TRANSFER_ADMIN_CODE_TYPE) {
            throw InternalErrorException("Code type is not transfer admin.")
        }
        val memberId = if (orgMemberService.isMemberOf(info.orgId, agentId)) {
            orgMemberService.getMemberId(info.orgId, agentId)!!
        } else {
            orgMemberService.addMember(info.orgId, agentId).id
        }
        orgMemberService.transferAdmin(info.oldAdminMemberId!!, memberId)
        orgCodeService.deleteCode(opcode)

        return success()
    }
}