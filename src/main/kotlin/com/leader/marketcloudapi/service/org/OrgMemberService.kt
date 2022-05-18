package com.leader.marketcloudapi.service.org

import com.leader.marketcloudapi.data.agent.Agent
import com.leader.marketcloudapi.data.org.OrgMember
import com.leader.marketcloudapi.data.org.OrgMemberRepository
import com.leader.marketcloudapi.data.org.Organization
import com.leader.marketcloudapi.service.agent.AgentService
import com.leader.marketcloudapi.util.InternalErrorException
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OrgMemberService @Autowired constructor(
    private val orgMemberRepository: OrgMemberRepository,
    private val agentService: AgentService
) {

    companion object {
        const val ADMIN_ROLE_NAME = OrgMember.ADMIN
    }

    fun isMemberOf(orgId: ObjectId, agentId: ObjectId): Boolean {
        return orgMemberRepository.existsByOrgIdAndAgentId(orgId, agentId)
    }

    fun assertIsMember(orgId: ObjectId, agentId: ObjectId) {
        if (!isMemberOf(orgId, agentId)) {
            throw InternalErrorException("Agent is not a member of the organization.")
        }
    }

    fun isAdminOf(orgId: ObjectId, agentId: ObjectId): Boolean {
        return isAdmin(getMemberId(orgId, agentId))
    }

    fun assertIsAdminOf(orgId: ObjectId, agentId: ObjectId) {
        if (!isAdminOf(orgId, agentId)) {
            throw InternalErrorException("Agent is not an admin of the organization.")
        }
    }

    fun isAdmin(memberId: ObjectId?): Boolean {
        return orgMemberRepository.existsByIdAndRolesContaining(memberId ?: return false, ADMIN_ROLE_NAME)
    }

    fun assertIsAdmin(memberId: ObjectId?) {
        if (!isAdmin(memberId)) {
            throw InternalErrorException("Agent is not an admin of the organization.")
        }
    }

    fun getMemberId(orgId: ObjectId, agentId: ObjectId): ObjectId? {
        return orgMemberRepository.findByOrgIdAndAgentId(orgId, agentId)?.id
    }

    fun getOrgMembers(orgId: ObjectId): List<Agent> {
        return orgMemberRepository.lookupByOrgId(orgId)
    }

    fun getAgentOrganizations(agentId: ObjectId): List<Organization> {
        return orgMemberRepository.lookupByAgentId(agentId)
    }

    private fun createMember(orgId: ObjectId, agentId: ObjectId, roles: MutableList<String>): OrgMember {
        if (isMemberOf(orgId, agentId)) {
            throw InternalErrorException("Agent is already a member of the organization.")
        }
        val orgMember = OrgMember()
        orgMember.orgId = orgId
        orgMember.agentId = agentId
        orgMember.roles = roles
        val member = orgMemberRepository.save(orgMember)
        if (agentService.getAgentDisplayOrgId(agentId) == null) {
            agentService.updateAgentDisplayOrgId(agentId, orgId)
        }
        return member
    }

    fun addAdmin(orgId: ObjectId, agentId: ObjectId): OrgMember {
        return createMember(orgId, agentId, mutableListOf(ADMIN_ROLE_NAME))
    }

    fun addMember(orgId: ObjectId, agentId: ObjectId): OrgMember {
        return createMember(orgId, agentId, mutableListOf())
    }

    fun transferAdmin(oldAdminMemberId: ObjectId, newAdminMemberId: ObjectId) {
        val oldAdminMember = orgMemberRepository.findById(oldAdminMemberId).orElse(null)
        val newAdminMember = orgMemberRepository.findById(newAdminMemberId).orElse(null)

        if (oldAdminMember == null || newAdminMember == null) {
            throw InternalErrorException("Member not found.")
        }

        if (oldAdminMember.orgId != newAdminMember.orgId) {
            throw InternalErrorException("Members are not part of the same organization.")
        }

        if (!oldAdminMember.roles.contains(ADMIN_ROLE_NAME) || newAdminMember.roles.contains(ADMIN_ROLE_NAME)) {
            throw InternalErrorException("Member is not an admin or new member is an admin.")
        }

        oldAdminMember.roles.remove(ADMIN_ROLE_NAME)
        newAdminMember.roles.add(ADMIN_ROLE_NAME)

        orgMemberRepository.save(oldAdminMember)
        orgMemberRepository.save(newAdminMember)
    }

    fun removeMember(memberId: ObjectId) {
        orgMemberRepository.findById(memberId).ifPresent { orgMember ->
            orgMemberRepository.deleteById(memberId)
            val agentId = orgMember.agentId
            val orgId = orgMember.orgId
            if (agentService.getAgentDisplayOrgId(agentId) == orgId) {
                agentService.updateAgentDisplayOrgId(agentId, null)
            }
        }
    }
}