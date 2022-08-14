package com.leader.marketcloudapi.data.agent

import com.leader.marketcloudapi.data.org.Organization
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository

@Document(collection = "agent_cards")
class AgentCard {

    @Id
    lateinit var id: ObjectId
    lateinit var agentId: ObjectId
    var name: String = ""
    var title: String = ""
    var phone: String = ""
    var email: String = ""
    var orgId: ObjectId? = null
    var backgroundUrl: String? = null
}

class AgentCardSummary {

    @Id
    lateinit var id: ObjectId
    lateinit var agentId: ObjectId
    var name: String = ""
    var title: String = ""
    var phone: String = ""
    var email: String = ""
    var orgId: ObjectId? = null
    var orgInfo: Organization? = null
    var backgroundUrl: String? = null
}

interface AgentCardRepository : MongoRepository<AgentCard, ObjectId> {

    @Aggregation(pipeline = [
        "{ \$match: ?0 }",
        "{ \$lookup: { \$from: 'org_list', \$localField: 'orgId', \$foreignField: '_id', \$as: 'orgInfo' } }",
        "{ \$unwind: { path: '\$orgInfo', preserveNullAndEmptyArrays: true } }",
    ])
    fun lookupByQuery(query: org.bson.Document): List<AgentCardSummary>

    fun existsByAgentIdAndId(agentId: ObjectId, id: ObjectId): Boolean

    fun deleteByAgentIdAndId(agentId: ObjectId, id: ObjectId)
}

fun AgentCardRepository.lookupByAgentId(agentId: ObjectId): List<AgentCardSummary> {
    return lookupByQuery(org.bson.Document("agentId", agentId))
}

fun AgentCardRepository.lookupById(id: ObjectId): AgentCardSummary? {
    return lookupByQuery(org.bson.Document("_id", id)).firstOrNull()
}
