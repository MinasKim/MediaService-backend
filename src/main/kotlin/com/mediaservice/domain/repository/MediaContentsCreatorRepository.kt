package com.mediaservice.domain.repository

import com.mediaservice.domain.MediaContentsCreator
import com.mediaservice.domain.MediaContentsCreatorTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.springframework.stereotype.Repository

@Repository
class MediaContentsCreatorRepository {

    fun save(mediaContentsCreator: MediaContentsCreator) {
        MediaContentsCreatorTable.insert {
            it[mediaContents] = mediaContentsCreator.mediaContents.id
            it[creator] = mediaContentsCreator.creator.id
        }
    }

    fun delete(mediaContentsCreator: MediaContentsCreator) {
        MediaContentsCreatorTable.deleteWhere {
            (MediaContentsCreatorTable.mediaContents eq mediaContentsCreator.mediaContents.id) and
                (MediaContentsCreatorTable.creator eq mediaContentsCreator.creator.id)
        }
    }
}
