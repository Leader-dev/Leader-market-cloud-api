package com.leader.marketcloudapi.service.org

import com.leader.marketcloudapi.data.org.*
import com.leader.marketcloudapi.util.InternalErrorException
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OrganizationService @Autowired constructor(
    private val orgRepository: OrganizationRepository
) {

    private fun copyValidFieldsTo(target: Organization, source: Organization) {
        target.name = source.name
        target.description = source.description
        target.slogan = source.slogan
    }

    fun listOrganizations(): List<OrgSummary> = orgRepository.lookupAll()

    fun getOrganization(id: ObjectId): OrgSummary? = orgRepository.lookupById(id)

    fun getOrganizationForce(id: ObjectId): OrgSummary = getOrganization(id) ?: throw InternalErrorException("Organization not found")

    fun createOrganization(org: Organization): Organization {
        val newOrg = Organization()
        copyValidFieldsTo(newOrg, org)
        return orgRepository.save(newOrg)
    }

    fun updateOrganization(id: ObjectId, info: Organization): Organization {
        val org = orgRepository.findById(id).orElseThrow {
            InternalErrorException("Organization not found")
        }
        copyValidFieldsTo(org, info)
        orgRepository.save(org)
        return org
    }

    fun updateOrganizationAvatar(id: ObjectId, avatarUrl: String): Organization {
        val org = orgRepository.findById(id).orElseThrow {
            InternalErrorException("Organization not found")
        }
        org.avatarUrl = avatarUrl
        orgRepository.save(org)
        return org
    }
}