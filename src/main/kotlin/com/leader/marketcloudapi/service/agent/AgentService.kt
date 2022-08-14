package com.leader.marketcloudapi.service.agent

import com.leader.marketcloudapi.data.agent.*
import com.leader.marketcloudapi.util.InternalErrorException
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AgentService @Autowired constructor(
    private val agentRepository: AgentRepository
) {

    private fun copyAgent(target: Agent, source: Agent) {
        target.name = source.name
        target.description = source.description
        target.orgId = source.orgId
    }

    fun createIfNotExists(userId: ObjectId): Agent {
        var agent = agentRepository.findByUserId(userId)
        if (agent == null) {
            agent = Agent()
            agent.userId = userId
            agentRepository.save(agent)
        }
        return agent
    }

    fun getAgentInfoByUserId(userId: ObjectId): Agent? {
        return agentRepository.findByUserId(userId)
    }

    fun getAgentInfoByUserIdForce(userId: ObjectId): Agent {
        return agentRepository.findByUserId(userId) ?: throw InternalErrorException("Agent not found")
    }

    fun updateAgentInfoByUserId(userId: ObjectId, agentInfo: Agent): Agent {
        val agent = getAgentInfoByUserIdForce(userId)
        copyAgent(agent, agentInfo)
        agentRepository.save(agent)
        return agent
    }

    fun updateAgentAvatarUrlByUserId(userId: ObjectId, avatarUrl: String): Agent {
        val agent = getAgentInfoByUserIdForce(userId)
        agent.avatarUrl = avatarUrl
        agentRepository.save(agent)
        return agent
    }

    fun listAgents(userAgentId: ObjectId?): List<AgentSummary> {
        return agentRepository.lookupAll(userAgentId)
    }

    fun getAgentInfo(id: ObjectId, userAgentId: ObjectId?): AgentSummary? {
        return agentRepository.lookupById(id, userAgentId)
    }

    fun getAgentDisplayOrgId(agentId: ObjectId): ObjectId? {
        val agentInfo = agentRepository.findById(agentId).orElseThrow()
        return agentInfo.orgId
    }

    fun updateAgentDisplayOrgId(agentId: ObjectId, displayOrgId: ObjectId?) {
        val agentInfo = agentRepository.findById(agentId).orElseThrow()
        agentInfo.orgId = displayOrgId
        agentRepository.save(agentInfo)
    }
}