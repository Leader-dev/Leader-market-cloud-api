package com.leader.marketcloudapi.graphql.project

import com.leader.marketcloudapi.data.project.Project
import com.leader.marketcloudapi.data.project.ProjectDetail
import com.leader.marketcloudapi.data.project.ProjectOverview
import com.leader.marketcloudapi.service.project.ProjectService
import com.leader.marketcloudapi.util.isRequiredArgument
import com.leader.marketcloudapi.util.success
import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Controller
class GProjectController @Autowired constructor(
    private val projectService: ProjectService
) {

    @SchemaMapping(typeName = "ProjectQuery")
    fun all(): List<ProjectOverview> = projectService.getProjects()

    @SchemaMapping(typeName = "ProjectQuery")
    fun byAgentId(@Argument agentId: ObjectId): List<ProjectOverview> = projectService.getValidProjects(agentId)

    @SchemaMapping(typeName = "ProjectQuery")
    fun byOrgId(@Argument orgId: ObjectId): List<ProjectOverview> = projectService.getValidProjectsByOrg(orgId)

    @SchemaMapping(typeName = "ProjectQuery")
    fun byId(@Argument id: ObjectId): ProjectDetail? = projectService.getProjectDetail(id)

    @SchemaMapping(typeName = "ProjectMutation")
    fun read(@Argument projectId: ObjectId): Int = projectService.incrementReadCount(projectId)
}
