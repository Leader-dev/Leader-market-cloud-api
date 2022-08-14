package com.leader.marketcloudapi.data.agent

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository

@Document(collection = "agent_card_share")
class AgentCardShare() {

    constructor(agentId: ObjectId, targetAgentId: ObjectId, cardId: ObjectId) : this() {
        this.agentId = agentId
        this.targetAgentId = targetAgentId
        this.cardId = cardId
    }

    @Id
    lateinit var id: ObjectId
    lateinit var agentId: ObjectId  // agent who interests other
    lateinit var targetAgentId: ObjectId  // agent who is being interested
    lateinit var cardId: ObjectId  // card id
}

interface AgentCardShareRepository : MongoRepository<AgentCardShare, ObjectId> {

    @Aggregation(pipeline = [
        """
            { ${"$"}match: ?0 }
        """,
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
            { ${"$"}unwind: "${"$"}agentInfo" }
        """,
        """
            { ${"$"}replaceRoot: { newRoot: '${"$"}agentInfo' } }
        """
    ])
    fun lookupByQuery(query: org.bson.Document): List<Agent>

    fun existsByAgentIdAndTargetAgentId(agentId: ObjectId, targetAgentId: ObjectId): Boolean
}

fun AgentCardShareRepository.lookupByAgentId(agentId: ObjectId): List<Agent> {
    return lookupByQuery(org.bson.Document("agentId", agentId))
}

fun AgentCardShareRepository.lookupByAgentIdOrTargetAgentId(targetAgentId: ObjectId): List<Agent> {
    return lookupByQuery(org.bson.Document("\$or", listOf(
        org.bson.Document("agentId", targetAgentId),
        org.bson.Document("targetAgentId", targetAgentId)
    )))
}
