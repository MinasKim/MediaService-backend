package com.mediaservice.domain.repository

import com.mediaservice.domain.MediaContentsActor
import com.mediaservice.domain.MediaContentsActorTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.springframework.stereotype.Repository

@Repository
class MediaContentsActorRepository {

    fun save(mediaContentsActor: MediaContentsActor) {
        MediaContentsActorTable.insert {
            it[mediaContents] = mediaContentsActor.mediaContents.id
            it[actor] = mediaContentsActor.actor.id
        }
    }

    fun delete(mediaContentsActor: MediaContentsActor) {
        MediaContentsActorTable.deleteWhere {
            (MediaContentsActorTable.mediaContents eq mediaContentsActor.mediaContents.id) and
                (MediaContentsActorTable.actor eq mediaContentsActor.actor.id)
        }
    }
}
