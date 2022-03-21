package com.mediaservice.domain.repository

import com.mediaservice.domain.Actor
import com.mediaservice.domain.ActorEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ActorRepository {
    fun save(actor: Actor): Actor {
        return ActorEntity.new {
            name = actor.name
            isDeleted = actor.isDeleted
        }.let { Actor.from(it) }
    }

    fun findById(id: UUID): Actor? {
        return ActorEntity.findById(id)?.let {
            Actor.from(it)
        }
    }

    fun findAll(): List<Actor>? {
        return ActorEntity.all().map {
            Actor.from(it)
        }.toList()
    }

    fun update(id: UUID, actor: Actor): Actor? {
        return ActorEntity.findById(id)?.let {
            it.name = actor.name
            return Actor.from(it)
        }
    }

    fun delete(id: UUID): Actor? {
        return ActorEntity.findById(id)?.let {
            it.isDeleted = true
            return Actor.from(it)
        }
    }
}
