package com.leader.marketcloudapi.service.agent

import com.leader.marketcloudapi.data.agent.*
import com.leader.marketcloudapi.util.InternalErrorException
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AgentCardService @Autowired constructor(
    private val agentCardRepository: AgentCardRepository
) {
    fun copyCard(target: AgentCard, source: AgentCard) {
        target.name = source.name
        target.title = source.title
        target.phone = source.phone
        target.email = source.email
        target.orgId = source.orgId
        target.backgroundUrl = source.backgroundUrl
    }

    fun getAgentCards(agentId: ObjectId): List<AgentCardSummary> {
        return agentCardRepository.lookupByAgentId(agentId)
    }

    fun getCard(id: ObjectId): AgentCardSummary? {
        return agentCardRepository.lookupById(id)
    }

    fun createCard(agentId: ObjectId, card: AgentCard): AgentCardSummary {
        val newCard = AgentCard()
        copyCard(newCard, card)
        newCard.agentId = agentId
        val id = agentCardRepository.insert(newCard).id
        return getCard(id)!!
    }

    fun updateCard(agentId: ObjectId, card: AgentCard): AgentCardSummary {
        val existingCard = agentCardRepository.findById(card.id)
            .orElseThrow { InternalErrorException("Card not found") }
        copyCard(existingCard, card)
        agentCardRepository.save(existingCard)
        return getCard(card.id)!!
    }

    fun hasCard(agentId: ObjectId, id: ObjectId): Boolean {
        return agentCardRepository.existsByAgentIdAndId(agentId, id)
    }

    fun deleteCard(agentId: ObjectId, id: ObjectId) {
        agentCardRepository.deleteByAgentIdAndId(agentId, id)
    }
}
