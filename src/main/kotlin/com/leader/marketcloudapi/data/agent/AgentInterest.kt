package com.leader.marketcloudapi.data.agent

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository

@Document(collection = "agent_interests")
class AgentInterest() {

    constructor(agentId: ObjectId, interestAgentId: ObjectId) : this() {
        this.agentId = agentId
        this.interestAgentId = interestAgentId
    }

    @Id
    lateinit var id: ObjectId
    lateinit var agentId: ObjectId  // agent who interests other
    lateinit var interestAgentId: ObjectId  // agent who is being interested
}

interface AgentInterestRepository : MongoRepository<AgentInterest, ObjectId> {

    @Aggregation(pipeline = [
        """
            {
                ${"$"}lookup: {
                    from: "agents",
                    localField: "agentId",
                    foreignField: "_id",
                    as: "agentInfo"
                }
            }
        """,
        """
            { ${"$"}unwind: "${'$'}agentInfo" }
        """,
        """
            { ${"$"}replaceRoot: { newRoot: '${"$"}agentInfo' } }
        """
    ])
    fun lookupByQuery(query: org.bson.Document): List<Agent>
}

fun AgentInterestRepository.lookupByAgentId(agentId: ObjectId): List<Agent> {
    return lookupByQuery(org.bson.Document("agentId", agentId))
}

fun AgentInterestRepository.lookupByInterestAgentId(interestAgentId: ObjectId): List<Agent> {
    return lookupByQuery(org.bson.Document("interestAgentId", interestAgentId))
}
