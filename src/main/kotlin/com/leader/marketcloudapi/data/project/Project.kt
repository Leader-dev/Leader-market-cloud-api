package com.leader.marketcloudapi.data.project

import com.leader.marketcloudapi.data.agent.Agent
import com.leader.marketcloudapi.data.org.Organization
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Date

@Document(collection = "project_list")
class Project {

    @Id
    lateinit var id: ObjectId
    lateinit var orgId: ObjectId
    lateinit var publisherAgentId: ObjectId

    lateinit var status: String
    var draft: Boolean = true
    var readCount: Int = 0
    lateinit var publishDate: Date
    lateinit var updateDate: Date

    lateinit var title: String
    lateinit var tags: MutableList<String>
    lateinit var content: String

    lateinit var startDate: Date
    lateinit var endDate: Date

    lateinit var coverUrl: String
    lateinit var imageUrls: MutableList<String>
}

class ProjectOverview {

    @Id
    lateinit var id: ObjectId
    lateinit var orgId: ObjectId
    lateinit var orgInfo: Organization
    lateinit var publisherAgentId: ObjectId
    lateinit var publisherAgentInfo: Agent

    lateinit var status: String
    var draft: Boolean = true
    var readCount: Int = 0
    lateinit var publishDate: Date
    lateinit var updateDate: Date

    lateinit var title: String
    lateinit var tags: MutableList<String>
    lateinit var content: String

    lateinit var startDate: Date
    lateinit var endDate: Date

    lateinit var coverUrl: String
}

class ProjectDetail {

    @Id
    lateinit var id: ObjectId
    lateinit var orgId: ObjectId
    lateinit var orgInfo: Organization
    lateinit var publisherAgentId: ObjectId
    lateinit var publisherAgentInfo: Agent

    lateinit var status: String
    var draft: Boolean = true
    var readCount: Int = 0
    lateinit var publishDate: Date
    lateinit var updateDate: Date

    lateinit var title: String
    lateinit var tags: MutableList<String>
    lateinit var content: String

    lateinit var startDate: Date
    lateinit var endDate: Date

    lateinit var coverUrl: String
    lateinit var imageUrls: MutableList<String>
}

interface ProjectRepository : MongoRepository<Project, ObjectId> {

    @Aggregation(pipeline = [
        "{ \$match: ?0 }",
        "{ \$lookup: { from: 'org_list', localField: 'orgId', foreignField: '_id', as: 'orgInfo' } }",
        "{ \$lookup: { from: 'agent_list', localField: 'publisherAgentId', foreignField: '_id', as: 'publisherAgentInfo' } }",
        "{ \$unwind: { path: '\$orgInfo', preserveNullAndEmptyArrays: true} }",
        "{ \$unwind: { path: '\$publisherAgentInfo', preserveNullAndEmptyArrays: true } }"
    ])
    fun <T> lookupByQuery(query: org.bson.Document, type: Class<T>): List<T>

    fun <T> findAllBy(type: Class<T>): List<T>

    fun findByOrgId(orgId: ObjectId): List<Project>

    fun findByPublisherAgentId(publisherAgentId: ObjectId): List<Project>

    fun findByPublisherAgentIdAndId(publisherAgentId: ObjectId, id: ObjectId): Project?

    fun deleteByPublisherAgentIdAndId(publisherAgentId: ObjectId, id: ObjectId)
}

fun ProjectRepository.lookupAll(): List<ProjectOverview> {
    return lookupByQuery(org.bson.Document("draft", false), ProjectOverview::class.java)
}

fun ProjectRepository.lookupByAgentIdAndDraft(agentId: ObjectId, draft: Boolean): List<ProjectOverview> {
    return lookupByQuery(org.bson.Document("publisherAgentId", agentId).append("draft", draft), ProjectOverview::class.java)
}

fun ProjectRepository.lookupByOrgIdAndDraft(orgId: ObjectId, draft: Boolean): List<ProjectOverview> {
    return lookupByQuery(org.bson.Document("orgId", orgId).append("draft", draft), ProjectOverview::class.java)
}

fun ProjectRepository.lookupById(id: ObjectId): ProjectDetail? {
    return lookupByQuery(org.bson.Document("_id", id), ProjectDetail::class.java).firstOrNull()
}
