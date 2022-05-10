package com.leader.marketcloudapi.graphql.agent

import com.leader.marketcloudapi.data.agent.Agent
import com.leader.marketcloudapi.service.agent.AgentInterestService
import com.leader.marketcloudapi.service.context.ContextService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class GAgentInterestController @Autowired constructor(
    private val agentInterestService: AgentInterestService,
    private val contextService: ContextService
) {

    @SchemaMapping(typeName = "CurrentAgentQuery")
    fun interests(): List<Agent> {
        val agentId = contextService.agentId
        return agentInterestService.getInterests(agentId)
    }

    @SchemaMapping(typeName = "CurrentAgentQuery")
    fun beingInterested(): List<Agent> {
        val agentId = contextService.agentId
        return agentInterestService.getBeingInterested(agentId)
    }

    @SchemaMapping(typeName = "CurrentAgentMutation")
    fun sendInterest(@Argument(name = "agentId") interestAgentId: ObjectId): Boolean {
        val agentId = contextService.agentId
        agentInterestService.sendInterest(agentId, interestAgentId)
        return true
    }
}