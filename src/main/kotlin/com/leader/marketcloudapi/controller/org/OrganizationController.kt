package com.leader.marketcloudapi.controller.org

import com.leader.marketcloudapi.service.org.OrganizationService
import com.leader.marketcloudapi.util.isRequiredArgument
import com.leader.marketcloudapi.util.success
import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/org")
class OrganizationController @Autowired constructor(
    private val orgService: OrganizationService
) {

    class QueryObject {
        var orgId: ObjectId? = null
    }

    @PostMapping("/list")
    fun listOrganizations(): Document {
        val orgList = orgService.listOrganizations()
        return success("list", orgList)
    }

    @PostMapping("/detail")
    fun getOrganizationDetail(@RequestBody queryObject: QueryObject): Document {
        val orgId = queryObject.orgId.isRequiredArgument("orgId")
        val orgDetail = orgService.getOrganization(orgId)
        return success("detail", orgDetail)
    }
}