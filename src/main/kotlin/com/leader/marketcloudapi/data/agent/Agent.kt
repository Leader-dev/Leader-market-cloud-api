package com.leader.marketcloudapi.data.agent

import com.leader.marketcloudapi.data.org.Organization
import com.leader.marketcloudapi.data.project.Project
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository

@Document(collection = "agent_list")
class Agent() {

    constructor(userId: ObjectId) : this() {
        this.userId = userId
    }

    @Id
    lateinit var id: ObjectId
    lateinit var userId: ObjectId
    var orgId: ObjectId? = null

    var name: String = ""
    var description: String = ""

    var showContact: Boolean = true
    var phone: String = ""
    var email: String = ""

    var avatarUrl: String = ""
}

class AgentSummary {

    @Id
    lateinit var id: ObjectId
    lateinit var userId: ObjectId
    var orgId: ObjectId? = null
    var orgInfo: Organization? = null

    var name: String = ""
    var description: String = ""

    var showContact: Boolean = true
    var phone: String = ""
    var email: String = ""

    var avatarUrl: String = ""

    lateinit var projects: List<Project>
    var readCount: Int = 0
    var projectCount: Int = 0
}

interface AgentRepository : MongoRepository<Agent, ObjectId> {

    fun existsByUserId(userId: ObjectId): Boolean

    fun findByUserId(userId: ObjectId): Agent?

    // TODO use aggregation stage to get the project count and read count
    @Aggregation(pipeline = [
        "{ \$match: ?0 }",
        "{ \$lookup: { from: 'org_list', localField: 'orgId', foreignField: '_id', as: 'orgInfo' } }",
        "{ \$lookup: { from: 'project_list', localField: 'id', foreignField: 'publisherAgentId', as: 'projects' } }",
        "{ \$unwind: { path: '\$orgInfo', preserveNullAndEmptyArrays: true } }",
    ])
    fun lookupByQuery(query: org.bson.Document): List<AgentSummary>
}

fun AgentRepository.lookupByQueryFull(query: org.bson.Document): List<AgentSummary> {
    val temp = lookupByQuery(query)
    temp.forEach { agent ->
        agent.readCount = 0
        agent.projectCount = 0
        agent.projects.filter { !it.draft }.forEach {
            agent.readCount += it.readCount
            agent.projectCount += 1
        }
        agent.projects = emptyList()  // avoid returning excessive data
    }
    return lookupByQuery(query)
}

fun AgentRepository.lookupById(id: ObjectId): AgentSummary? {
    return lookupByQueryFull(org.bson.Document("_id", id)).firstOrNull()
}

fun AgentRepository.lookupAll(): List<AgentSummary> {
    return lookupByQueryFull(org.bson.Document())
}
