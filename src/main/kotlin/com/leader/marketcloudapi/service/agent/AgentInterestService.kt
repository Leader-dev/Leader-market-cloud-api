package com.leader.marketcloudapi.service.agent

import com.leader.marketcloudapi.data.agent.*
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AgentInterestService @Autowired constructor(
    private val agentInterestRepository: AgentInterestRepository
) {

    fun getInterests(agentId: ObjectId): List<Agent> {
        return agentInterestRepository.lookupByAgentId(agentId)
    }

    fun getBeingInterested(interestAgentId: ObjectId): List<Agent> {
        return agentInterestRepository.lookupByInterestAgentId(interestAgentId)
    }

    fun sendInterest(agentId: ObjectId, interestAgentId: ObjectId) {
        if (!agentInterestRepository.existsByAgentIdAndInterestAgentId(agentId, interestAgentId)) {
            agentInterestRepository.save(AgentInterest(agentId, interestAgentId))
        }
    }
}