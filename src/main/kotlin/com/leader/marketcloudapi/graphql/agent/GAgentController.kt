package com.leader.marketcloudapi.graphql.agent

import com.leader.marketcloudapi.data.agent.AgentSummary
import com.leader.marketcloudapi.service.agent.AgentService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class GAgentController @Autowired constructor(
    private val agentService: AgentService
) {

    @SchemaMapping(typeName = "AgentQuery")
    fun all(): List<AgentSummary> = agentService.listAgents()

    @SchemaMapping(typeName = "AgentQuery")
    fun byId(@Argument id: ObjectId): AgentSummary? = agentService.getAgentInfo(id)
}