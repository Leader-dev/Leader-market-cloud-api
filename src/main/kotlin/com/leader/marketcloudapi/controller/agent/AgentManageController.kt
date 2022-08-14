package com.leader.marketcloudapi.controller.agent

import com.leader.marketcloudapi.data.agent.Agent
import com.leader.marketcloudapi.mq.ImageInfoMessageQueue
import com.leader.marketcloudapi.service.agent.AgentService
import com.leader.marketcloudapi.service.context.ContextService
import com.leader.marketcloudapi.service.org.OrgMemberService
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
    private val orgMemberService: OrgMemberService,
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
        info.orgId?.let {
            if (!orgMemberService.isMemberOf(it, userId)) {
                throw InternalErrorException("User not in organization.")
            }
        }
        val agent = agentService.updateAgentInfoByUserId(userId, info)
        return success("detail", agent)
    }

    @PostMapping("/info/update/avatarUrl")
    fun updateAgentAvatar(@RequestBody queryObject: QueryObject): Document {
        val avatarUrl = queryObject.avatarUrl.isRequiredArgument("avatarUrl")

        imageInfoMessageQueue.assertImageUploaded(avatarUrl)

        val userId = contextService.userId  // use user id for performance
        val originalAvatarUrl = agentService.getAgentInfoByUserIdForce(userId).avatarUrl
        val agent = agentService.updateAgentAvatarUrlByUserId(userId, avatarUrl)

        imageInfoMessageQueue.deleteImage(originalAvatarUrl)
        imageInfoMessageQueue.confirmImageUploaded(avatarUrl)

        return success("detail", agent)
    }
}