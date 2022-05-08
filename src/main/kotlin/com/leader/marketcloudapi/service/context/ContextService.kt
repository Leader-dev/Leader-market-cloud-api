package com.leader.marketcloudapi.service.context

import com.leader.marketcloudapi.ThreadJWTData
import com.leader.marketcloudapi.service.agent.AgentService
import com.leader.marketcloudapi.service.org.OrgMemberService
import com.leader.marketcloudapi.util.InternalErrorException
import com.leader.marketcloudapi.util.UserAuthException
import com.leader.marketcloudapi.util.component.VolatileThreadData
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ContextService @Autowired constructor(
    private val threadJWTData: ThreadJWTData,
    private val volatileThreadData: VolatileThreadData,
    private val agentService: AgentService,
    private val orgMemberService: OrgMemberService
) {

    companion object {
        private const val USER_ID_KEY = "userId"
        private const val ORG_ID_KEY = "orgId"
    }

    val userId: ObjectId
        get() {
            val stringObjectId = threadJWTData[USER_ID_KEY] as? String ?: throw UserAuthException()
            return ObjectId(stringObjectId)
        }

    val hasUserId: Boolean
        get() = threadJWTData.containsKey(USER_ID_KEY)

    val agentId: ObjectId
        get() {
            return agentService.createIfNotExists(userId).id
        }

    var orgId: ObjectId
        get() {
            if (!volatileThreadData.containsKey(ORG_ID_KEY)) {
                throw InternalErrorException("orgId is required.")
            }
            return volatileThreadData[ORG_ID_KEY, ObjectId::class.java]
        }
        set(value) {
            volatileThreadData[ORG_ID_KEY] = value
        }

    val memberId: ObjectId
        get() {
            return orgMemberService.getMemberId(orgId, agentId)
                ?: throw InternalErrorException("Agent is not a member of the organization.")
        }
}