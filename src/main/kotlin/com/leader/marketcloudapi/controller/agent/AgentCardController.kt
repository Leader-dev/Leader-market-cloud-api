package com.leader.marketcloudapi.controller.agent

import com.leader.marketcloudapi.data.agent.AgentCard
import com.leader.marketcloudapi.mq.ImageInfoMessageQueue
import com.leader.marketcloudapi.service.agent.AgentCardService
import com.leader.marketcloudapi.service.agent.AgentCardShareService
import com.leader.marketcloudapi.service.context.ContextService
import com.leader.marketcloudapi.util.*
import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/agent/card")
class AgentCardController @Autowired constructor(
    private val agentCardService: AgentCardService,
    private val agentCardShareService: AgentCardShareService,
    private val contextService: ContextService,
    private val imageInfoMessageQueue: ImageInfoMessageQueue
) {

    class QueryObject {
        var info: AgentCard? = null
        var cardId: ObjectId? = null
        var targetAgentId: ObjectId? = null
    }

    @PostMapping("/create")
    fun createCard(queryObject: QueryObject): Document {
        val agentId = contextService.agentId
        val info = queryObject.info.isRequiredArgument("info")
        imageInfoMessageQueue.assertImageUploaded(info.backgroundUrl)
        val detail = agentCardService.createCard(agentId, info)
        imageInfoMessageQueue.confirmImageUploaded(info.backgroundUrl)
        return success("detail", detail)
    }

    @PostMapping("/delete")
    fun deleteCard(queryObject: QueryObject): Document {
        val agentId = contextService.agentId
        val cardId = queryObject.cardId.isRequiredArgument("cardId")
        return success("detail", agentCardService.deleteCard(agentId, cardId))
    }

    @PostMapping("/list")
    fun listCard(queryObject: QueryObject): Document {
        val agentId = contextService.agentId
        return success("list", agentCardService.getAgentCards(agentId))
    }

    @PostMapping("/update")
    fun updateCard(queryObject: QueryObject): Document {
        val agentId = contextService.agentId
        val info = queryObject.info.isRequiredArgument("info")
        val originalInfo = agentCardService.getCard(info.id)
            ?: throw InternalErrorException("card not found")

        val toDelete = originalInfo.backgroundUrl.oneMinus(info.backgroundUrl)
        val toAdd = info.backgroundUrl.oneMinus(originalInfo.backgroundUrl)

        imageInfoMessageQueue.assertImageUploaded(toAdd)

        val detail = agentCardService.updateCard(agentId, info)

        imageInfoMessageQueue.confirmImageUploaded(toAdd)
        imageInfoMessageQueue.deleteImage(toDelete)

        return success("detail", detail)
    }

    @PostMapping("/list/shared")
    fun listSharedFavorite(): Document {
        val agentId = contextService.agentId
        return success("list", agentCardShareService.getSharedCards(agentId))
    }

    @PostMapping("/send")
    fun createSharedFavorite(queryObject: QueryObject): Document {
        val agentId = contextService.agentId
        val targetAgentId = queryObject.targetAgentId.isRequiredArgument("targetAgentId")
        val cardId = queryObject.cardId.isRequiredArgument("cardId")
        if (!agentCardService.hasCard(agentId, cardId)) {
            throw InternalErrorException("card not found")
        }
        agentCardShareService.sendCard(agentId, targetAgentId, cardId)
        return success()
    }
}