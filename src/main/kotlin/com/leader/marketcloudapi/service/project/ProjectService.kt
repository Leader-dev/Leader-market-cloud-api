package com.leader.marketcloudapi.service.project

import com.leader.marketcloudapi.data.project.*
import com.leader.marketcloudapi.util.InternalErrorException
import com.leader.marketcloudapi.util.component.DateUtil
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProjectService @Autowired constructor(
    private val projectRepository: ProjectRepository,
    private val dateUtil: DateUtil
) {

    private fun copyProjectInfo(source: Project, target: Project) {
        target.status = source.status
        target.title = source.title
        target.tags = source.tags
        target.content = source.content
        target.startDate = source.startDate
        target.endDate = source.endDate
        target.coverUrl = source.coverUrl
        target.imageUrls = source.imageUrls
    }

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
        val newProject = Project()
        newProject.publisherAgentId = publisherAgentId
        copyProjectInfo(project, newProject)
        if (!project.draft) {
            newProject.publishDate = dateUtil.getCurrentDate()
        }
        newProject.updateDate = dateUtil.getCurrentDate()
        projectRepository.insert(newProject)
    }

    fun updateProject(agentId: ObjectId, project: Project) {
        val existingProject = projectRepository.findByPublisherAgentIdAndId(agentId, project.id)
            ?: throw InternalErrorException("Project not found")
        copyProjectInfo(project, existingProject)
        if (!project.draft) {
            existingProject.publishDate = dateUtil.getCurrentDate()
        }
        existingProject.updateDate = dateUtil.getCurrentDate()
        projectRepository.save(existingProject)
    }

    fun deleteProject(publisherAgentId: ObjectId, projectId: ObjectId) {
        projectRepository.deleteByPublisherAgentIdAndId(publisherAgentId, projectId)
    }
}