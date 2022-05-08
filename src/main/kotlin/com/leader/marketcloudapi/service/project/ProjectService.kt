package com.leader.marketcloudapi.service.project

import com.leader.marketcloudapi.data.project.*
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProjectService @Autowired constructor(
    private val projectRepository: ProjectRepository
) {

    fun getProjects(): List<ProjectOverview> {
        return projectRepository.lookupAll()
    }

    fun getValidProjects(agentId: ObjectId): List<ProjectOverview> {
        return projectRepository.lookupByAgentIdAndDraft(agentId, false)
    }

    fun getValidProjectsByOrg(orgId: ObjectId): List<ProjectOverview> {
        return projectRepository.lookupByOrgIdAndDraft(orgId, false)
    }

    fun getDrafts(agentId: ObjectId): List<ProjectOverview> {
        return projectRepository.lookupByAgentIdAndDraft(agentId, true)
    }

    fun getProjectDetail(projectId: ObjectId): ProjectDetail? {
        return projectRepository.lookupById(projectId)
    }

    fun incrementReadCount(projectId: ObjectId) {
        projectRepository.findById(projectId).ifPresent {
            it.readCount++
            projectRepository.save(it)
        }
    }

    fun publishProject(publisherAgentId: ObjectId, project: Project) {
        project.publisherAgentId = publisherAgentId
        projectRepository.save(project)
    }

    fun updateProject(agentId: ObjectId, project: Project) {
        // TODO lookup project by id and check if agentId is the same as the one in the project
        projectRepository.save(project)
    }

    fun deleteProject(publisherAgentId: ObjectId, projectId: ObjectId) {
        projectRepository.deleteByPublisherAgentIdAndId(publisherAgentId, projectId)
    }
}