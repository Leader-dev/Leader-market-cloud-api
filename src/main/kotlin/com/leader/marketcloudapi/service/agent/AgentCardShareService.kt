package com.leader.marketcloudapi.service.agent

import com.leader.marketcloudapi.data.agent.*
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AgentCardShareService @Autowired constructor(
    private val agentCardShareRepository: AgentCardShareRepository
) {

    fun getSharedCards(agentId: ObjectId): List<Agent> {
        return agentCardShareRepository.lookupByAgentIdOrTargetAgentId(agentId)
    }

    fun sendCard(agentId: ObjectId, targetAgentId: ObjectId, cardId: ObjectId) {
        if (!agentCardShareRepository.existsByAgentIdAndTargetAgentId(agentId, targetAgentId)) {
            agentCardShareRepository.save(AgentCardShare(agentId, targetAgentId, cardId))
        }
    }
}