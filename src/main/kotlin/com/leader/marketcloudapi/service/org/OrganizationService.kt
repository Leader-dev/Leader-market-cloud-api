package com.leader.marketcloudapi.service.org

import com.leader.marketcloudapi.data.org.Organization
import com.leader.marketcloudapi.data.org.OrganizationRepository
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
    }

    fun listOrganizations(): List<Organization> = orgRepository.findAll()

    fun getOrganization(id: ObjectId): Organization? = orgRepository.findById(id).orElse(null)

    fun createOrganization(org: Organization): Organization {
        val newOrg = Organization()
        copyValidFieldsTo(newOrg, org)
        return orgRepository.save(newOrg)
    }

    fun updateOrganization(id: ObjectId, info: Organization) {
        orgRepository.findById(id).ifPresent { org ->
            copyValidFieldsTo(org, info)
            orgRepository.save(org)
        }
    }
}