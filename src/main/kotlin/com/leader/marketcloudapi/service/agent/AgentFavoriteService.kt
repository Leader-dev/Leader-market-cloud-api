package com.leader.marketcloudapi.service.agent

import com.leader.marketcloudapi.data.agent.Agent
import com.leader.marketcloudapi.data.agent.AgentFavorite
import com.leader.marketcloudapi.data.agent.AgentFavoriteRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AgentFavoriteService @Autowired constructor(
    private val agentFavoriteRepository: AgentFavoriteRepository
) {

    fun getFavorites(agentId: ObjectId): List<Agent> {
        return agentFavoriteRepository.lookByAgentId(agentId)
    }

    fun addFavorite(agentId: ObjectId, favoriteAgentId: ObjectId) {
        if (!agentFavoriteRepository.existsByAgentIdAndFavoriteAgentId(agentId, favoriteAgentId)) {
            agentFavoriteRepository.save(AgentFavorite(agentId, favoriteAgentId))
        }
    }

    fun removeFavorite(agentId: ObjectId, favoriteAgentId: ObjectId) {
        agentFavoriteRepository.removeByAgentIdAndFavoriteAgentId(agentId, favoriteAgentId)
    }
}