package com.leader.marketcloudapi.controller.org

import com.leader.marketcloudapi.data.org.Organization
import com.leader.marketcloudapi.mq.ImageInfoMessageQueue
import com.leader.marketcloudapi.service.context.ContextService
import com.leader.marketcloudapi.service.org.OrgMemberService
import com.leader.marketcloudapi.service.org.OrganizationService
import com.leader.marketcloudapi.util.InternalErrorException
import com.leader.marketcloudapi.util.data
import com.leader.marketcloudapi.util.isRequiredArgument
import com.leader.marketcloudapi.util.success
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/org/manage")
class OrgManageController @Autowired constructor(
    private val organizationService: OrganizationService,
    private val orgMemberService: OrgMemberService,
    private val contextService: ContextService,
    private val imageInfoMessageQueue: ImageInfoMessageQueue
) {

    class QueryObject {
        var info: Organization? = null
        var avatarUrl: String? = null
    }

    @PostMapping("/roles")
    fun organizationRoles(): Document {
        val hasUserId = contextService.hasUserId
        if (!hasUserId) {
            return success().data(
                "isMember", false,
                "isAdmin", false
            )
        }

        val orgId = contextService.orgId
        val agentId = contextService.agentId
        if (!orgMemberService.isMemberOf(orgId, agentId)) {
            return success().data(
                "isMember", false,
                "isAdmin", false
            )
        }

        val memberId = contextService.memberId
        val isAdmin = orgMemberService.isAdmin(memberId)
        return success().data(
            "isMember", true,
            "isAdmin", isAdmin
        )
    }

    @PostMapping("/create")
    fun create(@RequestBody queryObject: QueryObject): Document {
        val agentId = contextService.agentId

        val info = queryObject.info.isRequiredArgument("info")

        val avatarUrl = info.avatarUrl
        imageInfoMessageQueue.assertImageUploaded(avatarUrl)

        val org = organizationService.createOrganization(info)
        orgMemberService.addAdmin(org.id, agentId)

        imageInfoMessageQueue.confirmImageUploaded(avatarUrl)

        return success("detail", organizationService.getOrganization(org.id))
    }

    @PostMapping("/list")
    fun list(): Document {
        val agentId = contextService.agentId

        val orgList = orgMemberService.getAgentOrganizations(agentId)

        return success("list", orgList)
    }

    @PostMapping("/members")
    fun listMembers(): Document {
        val orgId = contextService.orgId
        val agentId = contextService.agentId

        orgMemberService.assertIsAdminOf(orgId, agentId)

        val agentList = orgMemberService.getOrgMembers(orgId)

        return success("members", agentList)
    }

    @PostMapping("/update")
    fun update(@RequestBody queryObject: QueryObject): Document {
        val memberId = contextService.memberId
        orgMemberService.assertIsAdmin(memberId)

        val orgId = contextService.orgId
        val info = queryObject.info.isRequiredArgument("info")
        val org = organizationService.updateOrganization(orgId, info)

        return success("detail", organizationService.getOrganization(org.id))
    }

    @PostMapping("/update/avatar")
    fun updateAvatar(@RequestBody queryObject: QueryObject): Document {
        val memberId = contextService.memberId
        orgMemberService.assertIsAdmin(memberId)

        val avatarUrl = queryObject.avatarUrl.isRequiredArgument("avatarUrl")

        imageInfoMessageQueue.assertImageUploaded(avatarUrl)

        val orgId = contextService.orgId
        val originalAvatarUrl = organizationService.getOrganizationForce(orgId).avatarUrl
        val org = organizationService.updateOrganizationAvatar(orgId, avatarUrl)

        imageInfoMessageQueue.deleteImage(originalAvatarUrl)
        imageInfoMessageQueue.confirmImageUploaded(avatarUrl)

        return success("detail", organizationService.getOrganization(org.id))
    }

    @PostMapping("/quit")
    fun quitOrganization(): Document {
        val memberId = contextService.memberId
        if (orgMemberService.isAdmin(memberId)) {
            throw InternalErrorException("Admin cannot quit organization.")
        }
        orgMemberService.removeMember(memberId)
        return success()
    }
}