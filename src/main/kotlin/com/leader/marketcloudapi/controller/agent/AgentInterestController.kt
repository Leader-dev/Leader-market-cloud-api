package com.leader.marketcloudapi.controller.agent

import com.leader.marketcloudapi.service.agent.AgentInterestService
import com.leader.marketcloudapi.service.context.ContextService
import com.leader.marketcloudapi.util.data
import com.leader.marketcloudapi.util.isRequiredArgument
import com.leader.marketcloudapi.util.success
import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/agents/interests")
class AgentInterestController @Autowired constructor(
    private val agentInterestService: AgentInterestService,
    private val contextService: ContextService
) {
    class QueryObject {
        val agentId: ObjectId? = null
    }

    @RequestMapping("/send")
    fun getAgentInterest(@RequestBody queryObject: QueryObject): Document {
        val agentId = contextService.agentId
        val interestAgentId = queryObject.agentId.isRequiredArgument("agentId")
        agentInterestService.sendInterest(agentId, interestAgentId)
        return success()
    }

    @RequestMapping("/list")
    fun listAgentInterest(): Document {
        val agentId = contextService.agentId
        return success().data(
            "interests", agentInterestService.getInterests(agentId),
            "beingInterested", agentInterestService.getBeingInterested(agentId)
        )
    }
}