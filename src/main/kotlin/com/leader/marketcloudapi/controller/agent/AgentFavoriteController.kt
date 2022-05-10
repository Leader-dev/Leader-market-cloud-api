package com.leader.marketcloudapi.controller.agent

import com.leader.marketcloudapi.service.agent.AgentFavoriteService
import com.leader.marketcloudapi.service.context.ContextService
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
@RequestMapping("/agent/favorite")
class AgentFavoriteController @Autowired constructor(
    private val agentFavoriteService: AgentFavoriteService,
    private val contextService: ContextService
) {

    class QueryObject {
        var agentId: ObjectId? = null
    }

    @PostMapping("/list")
    fun listFavorite(): Document {
        val agentId = contextService.agentId
        val favorites = agentFavoriteService.getFavorites(agentId)
        return success("list", favorites)
    }

    @PostMapping("/add")
    fun addFavorite(@RequestBody queryObject: QueryObject): Document {
        val agentId = contextService.agentId
        val favoriteAgentId = queryObject.agentId.isRequiredArgument("agentId")
        agentFavoriteService.addFavorite(agentId, favoriteAgentId)
        return success()
    }

    @PostMapping("/remove")
    fun removeFavorite(@RequestBody queryObject: QueryObject): Document {
        val agentId = contextService.agentId
        val favoriteAgentId = queryObject.agentId.isRequiredArgument("agentId")
        agentFavoriteService.removeFavorite(agentId, favoriteAgentId)
        return success()
    }
}