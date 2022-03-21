package com.mediaservice.domain.repository

import com.mediaservice.domain.Media
import com.mediaservice.domain.MediaEntity
import com.mediaservice.domain.MediaTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class MediaRepository {
    fun findById(id: UUID): Media? {
        return MediaEntity.findById(id)?.let {
            return Media.from(it)
        }
    }

    fun findByMediaSeriesId(id: UUID): List<Media> {
        return MediaEntity.find {
            MediaTable.mediaSeries eq id and (MediaTable.isDeleted eq false)
        }.sortedBy {
            it.order
        }.map {
            Media.from(it)
        }.toList()
    }

    fun save(media: Media): Media {
        media.id = (
            MediaTable.insert {
                it[name] = media.name
                it[synopsis] = media.synopsis
                it[order] = media.order
                it[url] = media.url
                it[thumbnail] = media.thumbnail
                it[runningTime] = media.runningTime
                it[isDeleted] = false
                it[mediaSeries] = media.mediaSeries.id
            } get MediaTable.id
            ).value
        return media
    }

    fun update(media: Media): Media? {
        return MediaEntity.findById(media.id!!)?.let {
            it.name = media.name
            it.synopsis = media.synopsis
            it.order = media.order
            it.url = media.url
            it.thumbnail = media.thumbnail
            it.runningTime = media.runningTime

            return Media.from(it)
        }
    }

    fun deleteById(id: UUID): Media? {
        return MediaEntity.findById(id)?.let {
            it.isDeleted = true
            return Media.from(it)
        }
    }
}
