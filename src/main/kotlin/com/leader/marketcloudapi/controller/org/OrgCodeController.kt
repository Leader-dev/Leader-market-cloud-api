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

    @PostMapping("/get-join")
    fun getOrganizationJoinCode(@RequestBody queryObject: OrgManageController.QueryObject): Document {
        val memberId = contextService.memberId
        orgMemberService.assertIsAdmin(memberId)

        val orgId = contextService.orgId
        val expiresIn = queryObject.expiresIn.isRequiredArgument("expiresIn")
        val ocode = orgCodeService.generateJoinCode(orgId, expiresIn)

        return success("ocode", ocode)
    }

    @PostMapping("/get-transfer")
    fun getOrganizationTransferCode(@RequestBody queryObject: OrgManageController.QueryObject): Document {
        val memberId = contextService.memberId
        orgMemberService.assertIsAdmin(memberId)

        val orgId = contextService.orgId
        val expiresIn = queryObject.expiresIn.isRequiredArgument("expiresIn")
        val ocode = orgCodeService.generateTransferAdminCode(orgId, memberId, expiresIn)

        return success("ocode", ocode)
    }

    @PostMapping("/info")
    fun getCodeInfo(@RequestBody queryObject: OrgManageController.QueryObject): Document {
        val ocode = queryObject.ocode.isRequiredArgument("ocode")
        val info = orgCodeService.getCodeInfo(ocode)
        return success("info", info)
    }

    @PostMapping("/join")
    fun joinOrganization(@RequestBody queryObject: OrgManageController.QueryObject): Document {
        val agentId = contextService.agentId

        val ocode = queryObject.ocode.isRequiredArgument("ocode")
        val info = orgCodeService.getCodeInfo(ocode)
        if (info.type != OrgCodeService.JOIN_CODE_TYPE) {
            throw InternalErrorException("Code type is not join.")
        }
        orgMemberService.addMember(info.orgId, agentId)

        return success()
    }

    @PostMapping("/claim-admin")
    fun claimOrganization(@RequestBody queryObject: OrgManageController.QueryObject): Document {
        val agentId = contextService.agentId

        val ocode = queryObject.ocode.isRequiredArgument("ocode")
        val info = orgCodeService.getCodeInfo(ocode)
        if (info.type != OrgCodeService.TRANSFER_ADMIN_CODE_TYPE) {
            throw InternalErrorException("Code type is not join.")
        }
        val memberId = if (orgMemberService.isMemberOf(info.orgId, agentId)) {
            orgMemberService.getMemberId(info.orgId, agentId)!!
        } else {
            orgMemberService.addMember(info.orgId, agentId).id
        }
        orgMemberService.transferAdmin(info.oldAdminMemberId!!, memberId)
        orgCodeService.deleteCode(ocode)

        return success()
    }
}