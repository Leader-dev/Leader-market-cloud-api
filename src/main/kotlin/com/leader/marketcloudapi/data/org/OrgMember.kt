package com.leader.marketcloudapi.data.org

import com.leader.marketcloudapi.data.agent.Agent
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository

@Document(collection = "org_members")
class OrgMember {

    companion object {
        const val MANAGER = "manager"
    }

    @Id
    lateinit var id: ObjectId
    lateinit var orgId: ObjectId
    lateinit var agentId: ObjectId
    lateinit var roles: MutableList<String>
}

interface OrgMemberRepository : MongoRepository<OrgMember, ObjectId> {

    fun existsByIdAndRolesContaining(id: ObjectId, role: String): Boolean

    fun existsByOrgIdAndAgentId(orgId: ObjectId, agentId: ObjectId): Boolean

    fun findByOrgIdAndAgentId(orgId: ObjectId, agentId: ObjectId): OrgMember?

    fun deleteByOrgIdAndAgentId(orgId: ObjectId, agentId: ObjectId)

    @Aggregation(pipeline = [
        """
            {
                ${"$"}match: {
                    orgId: ?0
                }
            }
        """,
        """
            {
                ${"$"}lookup: {
                    from: "agent_list",
                    localField: "agentId",
                    foreignField: "_id",
                    as: "agentInfo"
                }
            }
        """,
        "{ \$unwind: '\$agentInfo' }",
        "{ \$replaceRoot: { newRoot: '\$agentInfo' } }",
    ])
    fun lookupByOrgId(orgId: ObjectId): List<Agent>

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
                    from: "org_list",
                    localField: "orgId",
                    foreignField: "_id",
                    as: "orgInfo"
                }
            }
        """,
        "{ \$unwind: '\$orgInfo' }",
        "{ \$replaceRoot: { newRoot: '\$orgInfo' } }",
    ])
    fun lookupByAgentId(agentId: ObjectId): List<Organization>
}