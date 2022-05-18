package com.leader.marketcloudapi.graphql.org

import com.leader.marketcloudapi.data.org.OrgSummary
import com.leader.marketcloudapi.service.org.OrganizationService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class GOrganizationController @Autowired constructor(
    private val orgService: OrganizationService
) {

    @SchemaMapping(typeName = "OrgQuery")
    fun all(): List<OrgSummary> = orgService.listOrganizations()

    @SchemaMapping(typeName = "OrgQuery")
    fun byId(@Argument id: ObjectId): OrgSummary? = orgService.getOrganization(id)
}