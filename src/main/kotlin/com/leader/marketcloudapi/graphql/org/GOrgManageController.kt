package com.leader.marketcloudapi.graphql.org

import com.leader.marketcloudapi.data.org.Organization
import com.leader.marketcloudapi.service.context.ContextService
import com.leader.marketcloudapi.service.org.OrgMemberService
import com.leader.marketcloudapi.service.org.OrganizationService
import com.leader.marketcloudapi.util.InternalErrorException
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class GOrgManageController @Autowired constructor(
    private val organizationService: OrganizationService,
    private val orgMemberService: OrgMemberService,
    private val contextService: ContextService
) {

    @SchemaMapping(typeName = "OrgQuery")
    fun isMember(@Argument orgId: ObjectId): Boolean =
        contextService.hasUserId && orgMemberService.isMemberOf(orgId, contextService.agentId)

    @SchemaMapping(typeName = "OrgQuery")
    fun isAdmin(@Argument orgId: ObjectId): Boolean =
        contextService.hasUserId && orgMemberService.isAdminOf(orgId, contextService.agentId)

    @SchemaMapping(typeName = "OrgMutation")
    fun create(@Argument info: Organization): Organization {
        val agentId = contextService.agentId

        val org = organizationService.createOrganization(info)
        orgMemberService.addAdmin(org.id, agentId)

        return org
    }

    @SchemaMapping(typeName = "CurrentAgentQuery")
    fun list(): List<Organization> {
        val agentId = contextService.agentId
        return orgMemberService.getAgentOrganizations(agentId)
    }

    @SchemaMapping(typeName = "OrgMutation")
    fun update(@Argument info: Organization): Boolean {
        val memberId = contextService.memberId
        orgMemberService.assertIsAdmin(memberId)

        val orgId = contextService.orgId
        organizationService.updateOrganization(orgId, info)

        return true
    }

    @SchemaMapping(typeName = "OrgMutation")
    fun quit(): Boolean {
        val memberId = contextService.memberId
        if (orgMemberService.isAdmin(memberId)) {
            throw InternalErrorException("Admin cannot quit organization.")
        }
        orgMemberService.removeMember(memberId)
        return true
    }
}