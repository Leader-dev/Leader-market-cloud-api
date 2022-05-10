package com.leader.marketcloudapi.graphql.project

import com.leader.marketcloudapi.data.project.Project
import com.leader.marketcloudapi.data.project.ProjectOverview
import com.leader.marketcloudapi.service.context.ContextService
import com.leader.marketcloudapi.service.project.ProjectService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class GProjectManageController @Autowired constructor(
    private val projectService: ProjectService,
    private val contextService: ContextService
) {
    @SchemaMapping(typeName = "CurrentAgentQuery")
    fun projects(): List<ProjectOverview> {
        val agentId = contextService.agentId
        return projectService.getValidProjects(agentId)
    }

    @SchemaMapping(typeName = "CurrentAgentQuery")
    fun drafts(): List<ProjectOverview> {
        val agentId = contextService.agentId
        return projectService.getDrafts(agentId)
    }

    @SchemaMapping(typeName = "ProjectMutation")
    fun publish(@Argument info: Project): Project {
        val agentId = contextService.agentId
        return projectService.publishProject(agentId, info)
    }

    @SchemaMapping(typeName = "ProjectMutation")
    fun update(@Argument info: Project): Project {
        val agentId = contextService.agentId
        return projectService.updateProject(agentId, info)
    }

    @SchemaMapping(typeName = "ProjectMutation")
    fun delete(@Argument projectId: ObjectId): Boolean {
        val agentId = contextService.agentId
        projectService.deleteProject(agentId, projectId)
        return true
    }
}