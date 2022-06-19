package com.leader.marketcloudapi.data.agent

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository

@Document(collection = "agent_favorites")
class AgentFavorite() {

    constructor(agentId: ObjectId, favoriteAgentId: ObjectId) : this() {
        this.agentId = agentId
        this.favoriteAgentId = favoriteAgentId
    }

    @Id
    lateinit var id: ObjectId
    lateinit var agentId: ObjectId  // the agent who favorites others
    lateinit var favoriteAgentId: ObjectId  // the agent who is favorited
}

interface AgentFavoriteRepository : MongoRepository<AgentFavorite, ObjectId> {

    @Aggregation(pipeline = [
        """
        {
            ${"$"}match: {
                agentId: ?0
            }
        }
        """,
        """
        {
            ${"$"}lookup: {
                from: "agent_list",
                localField: "favoriteAgentId",
                foreignField: "_id",
                as: "agentInfo"
            }
        }
        """,
        """
        {
            ${"$"}unwind: "${"$"}agentInfo"
        }
        """,
        """
        {
            ${"$"}replaceRoot: { newRoot: '${"$"}agentInfo' }
        }
        """
    ])
    fun lookByAgentId(agentId: ObjectId): List<Agent>

    fun existsByAgentIdAndFavoriteAgentId(agentId: ObjectId, favoriteAgentId: ObjectId): Boolean

    fun removeByAgentIdAndFavoriteAgentId(agentId: ObjectId, favoriteAgentId: ObjectId)
}