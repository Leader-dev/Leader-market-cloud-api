package com.leader.marketcloudapi.controller.org

import com.leader.marketcloudapi.service.context.ContextService
import com.leader.marketcloudapi.service.org.OrgCodeService
import com.leader.marketcloudapi.service.org.OrgMemberService
import com.leader.marketcloudapi.util.InternalErrorException
import com.leader.marketcloudapi.util.isRequiredArgument
import com.leader.marketcloudapi.util.success
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/org/manage/code")
class OrgCodeController @Autowired constructor(
    private val orgMemberService: OrgMemberService,
    private val orgCodeService: OrgCodeService,
    private val contextService: ContextService
) {

    class QueryObject {
        var opcode: String? = null
        var expiresIn: Long? = null
    }

    @PostMapping("/get-join")
    fun getOrganizationJoinCode(@RequestBody queryObject: QueryObject): Document {
        val memberId = contextService.memberId
        orgMemberService.assertIsAdmin(memberId)

        val orgId = contextService.orgId
        val expiresIn = queryObject.expiresIn.isRequiredArgument("expiresIn")
        val opcode = orgCodeService.generateJoinCode(orgId, expiresIn)

        return success("opcode", opcode)
    }

    @PostMapping("/get-transfer")
    fun getOrganizationTransferCode(@RequestBody queryObject: QueryObject): Document {
        val memberId = contextService.memberId
        orgMemberService.assertIsAdmin(memberId)

        val orgId = contextService.orgId
        val expiresIn = queryObject.expiresIn.isRequiredArgument("expiresIn")
        val opcode = orgCodeService.generateTransferAdminCode(orgId, memberId, expiresIn)

        return success("opcode", opcode)
    }

    @PostMapping("/info")
    fun getCodeInfo(@RequestBody queryObject: QueryObject): Document {
        val opcode = queryObject.opcode.isRequiredArgument("opcode")
        val info = orgCodeService.getCodeInfo(opcode)
        return success("info", info)
    }

    @PostMapping("/join")
    fun joinOrganization(@RequestBody queryObject: QueryObject): Document {
        val agentId = contextService.agentId

        val opcode = queryObject.opcode.isRequiredArgument("opcode")
        val info = orgCodeService.getCodeInfo(opcode)
        if (info.type != OrgCodeService.JOIN_CODE_TYPE) {
            throw InternalErrorException("Code type is not join.")
        }
        orgMemberService.addMember(info.orgId, agentId)

        return success()
    }

    @PostMapping("/claim-admin")
    fun claimOrganization(@RequestBody queryObject: QueryObject): Document {
        val agentId = contextService.agentId

        val opcode = queryObject.opcode.isRequiredArgument("opcode")
        val info = orgCodeService.getCodeInfo(opcode)
        if (info.type != OrgCodeService.TRANSFER_ADMIN_CODE_TYPE) {
            throw InternalErrorException("Code type is not claim.")
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