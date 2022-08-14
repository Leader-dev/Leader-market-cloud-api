package com.leader.marketcloudapi.controller.project

import com.leader.marketcloudapi.data.project.Project
import com.leader.marketcloudapi.mq.ImageInfoMessageQueue
import com.leader.marketcloudapi.service.context.ContextService
import com.leader.marketcloudapi.service.project.ProjectService
import com.leader.marketcloudapi.util.InternalErrorException
import com.leader.marketcloudapi.util.isRequiredArgument
import com.leader.marketcloudapi.util.setMinus
import com.leader.marketcloudapi.util.success
import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/project/manage")
class ProjectManageController @Autowired constructor(
    private val projectService: ProjectService,
    private val contextService: ContextService,
    private val imageInfoMessageQueue: ImageInfoMessageQueue
) {
    class QueryObject {
        var projectId: ObjectId? = null
        var info: Project? = null
    }

    @PostMapping("/list")
    fun getProjectList(): Document {
        val agentId = contextService.agentId
        return success("list", projectService.getValidProjects(agentId))
    }

    @PostMapping("/publish")
    fun publishProject(@RequestBody queryObject: QueryObject): Document {
        val agentId = contextService.agentId
        val info = queryObject.info.isRequiredArgument("info")

        val newImages = listOf(info.coverUrl) + info.imageUrls
        imageInfoMessageQueue.assertImagesUploaded(newImages)

        val project = projectService.publishProject(agentId, info)

        imageInfoMessageQueue.confirmImagesUploaded(newImages)

        return success("detail", projectService.getProjectDetail(project.id))
    }

    @PostMapping("/update")
    fun updateProject(@RequestBody queryObject: QueryObject): Document {
        val agentId = contextService.agentId
        val info = queryObject.info.isRequiredArgument("info")

        val originalProject = projectService.getProjectDetail(info.id)
            ?: throw InternalErrorException("Project not found")
        val newImages = info.coverUrl.setMinus(originalProject.coverUrl) + info.imageUrls.setMinus(originalProject.imageUrls)
        val oldImages = originalProject.coverUrl.setMinus(info.coverUrl) + originalProject.imageUrls.setMinus(info.imageUrls)

        imageInfoMessageQueue.assertImagesUploaded(newImages)

        val project = projectService.updateProject(agentId, info)

        imageInfoMessageQueue.deleteImages(oldImages)
        imageInfoMessageQueue.confirmImagesUploaded(newImages)

        return success("detail", projectService.getProjectDetail(project.id))
    }

    @PostMapping("/drafts")
    fun getProjectDrafts(): Document {
        val agentId = contextService.agentId
        return success("drafts", projectService.getDrafts(agentId))
    }

    @PostMapping("/delete")
    fun deleteProject(@RequestBody queryObject: QueryObject): Document {
        val agentId = contextService.agentId
        val projectId = queryObject.projectId.isRequiredArgument("projectId")
        projectService.deleteProject(agentId, projectId)
        return success()
    }
}