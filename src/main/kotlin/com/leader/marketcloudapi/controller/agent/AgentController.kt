package com.leader.marketcloudapi.controller.agent

import com.leader.marketcloudapi.service.agent.AgentService
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
@RequestMapping("/agent")
class AgentController @Autowired constructor(
    private val agentService: AgentService
) {

    class QueryObject {
        val agentId: ObjectId? = null
    }

    @PostMapping("/list")
    fun getAllAgents(): Document {
        return success("list", agentService.listAgents())
    }

    @PostMapping("/detail")
    fun getAgentInfo(@RequestBody queryObject: QueryObject): Document {
        val agentId = queryObject.agentId.isRequiredArgument("agentId")
        return success("detail", agentService.getAgentInfo(agentId))
    }
}