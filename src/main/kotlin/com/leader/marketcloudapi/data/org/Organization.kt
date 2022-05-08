package com.leader.marketcloudapi.data.org

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository

@Document(collection = "org_list")
class Organization {

    @Id
    lateinit var id: ObjectId
    var name: String = ""
    var description: String = ""
    var avatarUrl: String = ""
    var certification: String = ""
}

interface OrganizationRepository : MongoRepository<Organization, ObjectId> {
}