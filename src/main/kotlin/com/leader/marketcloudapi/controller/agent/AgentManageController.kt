package com.leader.marketcloudapi.controller.agent

import com.leader.marketcloudapi.data.agent.Agent
import com.leader.marketcloudapi.mq.ImageInfoMessageQueue
import com.leader.marketcloudapi.service.agent.AgentService
import com.leader.marketcloudapi.service.context.ContextService
import com.leader.marketcloudapi.util.InternalErrorException
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
@RequestMapping("/agent/manage")
class AgentManageController @Autowired constructor(
    private val agentService: AgentService,
    private val contextService: ContextService,
    private val imageInfoMessageQueue: ImageInfoMessageQueue
) {

    class QueryObject {
        var info: Agent? = null
        var orgId: ObjectId? = null
        var avatarUrl: String? = null
    }

    @PostMapping("/id")
    fun getAgentId(): Document {
        val agentId = contextService.agentId
        return success("agentId", agentId)
    }

    @PostMapping("/info")
    fun getAgentInfo(): Document {
        val userId = contextService.userId  // use user id for performance
        val agentInfo = agentService.createIfNotExists(userId)
        return success("info", agentInfo)
    }

    @PostMapping("/info/update")
    fun updateAgentInfo(@RequestBody queryObject: QueryObject): Document {
        val info = queryObject.info.isRequiredArgument("info")
        val userId = contextService.userId  // use user id for performance
        agentService.updateAgentInfoByUserId(userId, info)
        return success()
    }

    @PostMapping("/info/update/orgId")
    fun updateAgentOrgId(@RequestBody queryObject: QueryObject): Document {
        val orgId = queryObject.orgId.isRequiredArgument("orgId")
        val userId = contextService.userId  // use user id for performance
        if (agentService.getAgentIdByUserId(userId) == null) {
            throw InternalErrorException("User not in organization.")
        }
        agentService.updateAgentOrgIdByUserId(userId, orgId)
        return success()
    }

    @PostMapping("/info/update/avatarUrl")
    fun updateAgentAvatar(@RequestBody queryObject: QueryObject): Document {
        val avatarUrl = queryObject.avatarUrl.isRequiredArgument("avatarUrl")

        imageInfoMessageQueue.assertImagesUploaded(listOf(avatarUrl))

        val userId = contextService.userId  // use user id for performance
        val originalAvatarUrl = agentService.getAgentInfoByUserIdForce(userId).avatarUrl
        agentService.updateAgentAvatarUrlByUserId(userId, avatarUrl)

        imageInfoMessageQueue.deleteImages(listOf(originalAvatarUrl))
        imageInfoMessageQueue.confirmImagesUploaded(listOf(avatarUrl))

        return success()
    }
}