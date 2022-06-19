package com.leader.marketcloudapi.controller.project

import com.leader.marketcloudapi.service.project.ProjectService
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
@RequestMapping("/project")
class ProjectController @Autowired constructor(
    private val projectService: ProjectService
) {
    class QueryObject {
        var projectId: ObjectId? = null
        var agentId: ObjectId? = null
        var orgId: ObjectId? = null
    }

    @PostMapping("/list/all")
    fun listProjects(): Document {
        val projects = projectService.getProjects()
        return success("list", projects)
    }

    @PostMapping("/list/agent")
    fun listAgentProjects(@RequestBody queryObject: QueryObject): Document {
        val agentId = queryObject.agentId.isRequiredArgument("agentId")
        val projects = projectService.getValidProjects(agentId)
        return success("list", projects)
    }

    @PostMapping("/list/org")
    fun listProject(@RequestBody queryObject: QueryObject): Document {
        val orgId = queryObject.orgId.isRequiredArgument("orgId")
        val projects = projectService.getValidProjectsByOrg(orgId)
        return success("list", projects)
    }

    @PostMapping("/detail")
    fun getProjectDetail(@RequestBody queryObject: QueryObject): Document {
        val projectId = queryObject.projectId.isRequiredArgument("projectId")
        val project = projectService.getProjectDetail(projectId)
        return success("detail", project)
    }

    @PostMapping("/read")
    fun incrementReadCount(@RequestBody queryObject: QueryObject): Document {
        val projectId = queryObject.projectId.isRequiredArgument("projectId")
        val count = projectService.incrementReadCount(projectId)
        return success("count", count)
    }
}
