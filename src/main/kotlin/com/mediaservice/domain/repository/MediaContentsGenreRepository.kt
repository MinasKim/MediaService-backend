package com.mediaservice.domain.repository

import com.mediaservice.domain.MediaContentsGenre
import com.mediaservice.domain.MediaContentsGenreTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.springframework.stereotype.Repository

@Repository
class MediaContentsGenreRepository {

    fun save(mediaContentsGenre: MediaContentsGenre) {
        MediaContentsGenreTable.insert {
            it[mediaContents] = mediaContentsGenre.mediaContents.id
            it[genre] = mediaContentsGenre.genre.id
        }
    }

    fun delete(mediaContentsGenre: MediaContentsGenre) {
        MediaContentsGenreTable.deleteWhere {
            (MediaContentsGenreTable.mediaContents eq mediaContentsGenre.mediaContents.id) and
                (MediaContentsGenreTable.genre eq mediaContentsGenre.genre.id)
        }
    }
}
