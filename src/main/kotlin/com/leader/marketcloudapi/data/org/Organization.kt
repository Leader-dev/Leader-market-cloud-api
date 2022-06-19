package com.leader.marketcloudapi.data.org

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository

@Document(collection = "org_list")
class Organization {

    @Id
    lateinit var id: ObjectId
    var name: String = ""
    var description: String = ""
    var slogan: String = ""
    var avatarUrl: String = ""
    var certification: String = ""
}

class OrgSummary {

    @Id
    lateinit var id: ObjectId
    var name: String = ""
    var description: String = ""
    var slogan: String = ""
    var avatarUrl: String = ""
    var certification: String = ""

    var memberCount: Int = 0
    var projectCount: Int = 0
}

interface OrganizationRepository : MongoRepository<Organization, ObjectId> {

    @Aggregation(pipeline = [
        "{ \$match: ?0 }",
        "{ \$lookup: { from: 'org_members', localField: '_id', foreignField: 'orgId', as: 'members' } }",
        "{ \$lookup: { from: 'project_list', localField: '_id', foreignField: 'orgId', as: 'projects' } }",
        "{ \$addFields: { projectCount: { \$size: '\$projects' }, memberCount: { \$size: '\$members' } } }",
    ])
    fun lookupByQuery(query: org.bson.Document): List<OrgSummary>
}

fun OrganizationRepository.lookupAll(): List<OrgSummary> {
    return lookupByQuery(org.bson.Document())
}

fun OrganizationRepository.lookupById(id: ObjectId): OrgSummary? {
    return lookupByQuery(org.bson.Document("_id", id)).firstOrNull()
}
