package com.leader.marketcloudapi.graphql.agent

import com.leader.marketcloudapi.data.agent.Agent
import com.leader.marketcloudapi.service.agent.AgentFavoriteService
import com.leader.marketcloudapi.service.context.ContextService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class GAgentFavoriteController @Autowired constructor(
    private val agentFavoriteService: AgentFavoriteService,
    private val contextService: ContextService
) {

    class QueryObject {
        var agentId: ObjectId? = null
    }

    @SchemaMapping(typeName = "CurrentAgentQuery")
    fun favorites(): List<Agent> {
        val agentId = contextService.agentId
        return agentFavoriteService.getFavorites(agentId)
    }

    @SchemaMapping(typeName = "CurrentAgentMutation")
    fun addFavorite(@Argument(name = "agentId") favoriteAgentId: ObjectId): Boolean {
        val agentId = contextService.agentId
        agentFavoriteService.addFavorite(agentId, favoriteAgentId)
        return true
    }

    @SchemaMapping(typeName = "CurrentAgentMutation")
    fun removeFavorite(@Argument(name = "agentId") favoriteAgentId: ObjectId): Boolean {
        val agentId = contextService.agentId
        agentFavoriteService.removeFavorite(agentId, favoriteAgentId)
        return true
    }
}