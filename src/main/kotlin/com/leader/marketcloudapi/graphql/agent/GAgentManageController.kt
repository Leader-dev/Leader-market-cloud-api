package com.leader.marketcloudapi.graphql.agent

import com.leader.marketcloudapi.data.agent.Agent
import com.leader.marketcloudapi.mq.ImageInfoMessageQueue
import com.leader.marketcloudapi.service.agent.AgentService
import com.leader.marketcloudapi.service.context.ContextService
import com.leader.marketcloudapi.util.InternalErrorException
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class GAgentManageController @Autowired constructor(
    private val agentService: AgentService,
    private val contextService: ContextService,
    private val imageInfoMessageQueue: ImageInfoMessageQueue
) {

    @SchemaMapping(typeName = "CurrentAgentQuery")
    fun info(): Agent {
        val userId = contextService.userId  // use user id for performance
        return agentService.createIfNotExists(userId)
    }

    @SchemaMapping(typeName = "CurrentAgentMutation")
    fun updateInfo(@Argument agent: Agent): Boolean{
        val userId = contextService.userId  // use user id for performance
        agentService.updateAgentInfoByUserId(userId, agent)
        return true
    }

    @SchemaMapping(typeName = "CurrentAgentMutation")
    fun updateOrgId(@Argument orgId: ObjectId): Boolean {
        val userId = contextService.userId  // use user id for performance
        if (agentService.getAgentIdByUserId(userId) == null) {
            throw InternalErrorException("User not in organization.")
        }
        agentService.updateAgentOrgIdByUserId(userId, orgId)
        return true
    }

    @SchemaMapping(typeName = "CurrentAgentMutation")
    fun updateAvatar(@Argument avatarUrl: String): Boolean {
        imageInfoMessageQueue.assertImagesUploaded(listOf(avatarUrl))

        val userId = contextService.userId  // use user id for performance
        val originalAvatarUrl = agentService.getAgentInfoByUserIdForce(userId).avatarUrl
        agentService.updateAgentAvatarUrlByUserId(userId, avatarUrl)

        imageInfoMessageQueue.deleteImages(listOf(originalAvatarUrl))
        imageInfoMessageQueue.confirmImagesUploaded(listOf(avatarUrl))

        return true
    }
}