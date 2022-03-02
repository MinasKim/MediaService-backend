package com.mediaservice.domain

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.util.UUID

object MediaContentsTable : UUIDTable(name = "TB_MEDIA_CONTENTS") {
    val title: Column<String> = varchar("title", 255)
    val synopsis: Column<String> = text("synopsis")
    val trailer: Column<String> = varchar("trailer", 255)
    val thumbnail: Column<String> = varchar("thumbnail", 255)
    val rate: Column<String> = varchar("age_rate", 255)
    val isSeries: Column<Boolean> = bool("is_series")
    val isDeleted: Column<Boolean> = bool("isDeleted")
}

class MediaContents(
    var id: UUID,
    var title: String,
    var synopsis: String,
    var trailer: String,
    var thumbnail: String,
    var rate: String,
    var isSeries: Boolean,
    var isDeleted: Boolean,
    var actorList: List<Actor>?,
    var genreList: List<Genre>?,
    var creatorList: List<Creator>?
) {
    companion object {
        const val DOMAIN = "MEDIA CONTENTS"

        fun from(mediaContentsEntity: MediaContentsEntity) = MediaContents(
            id = mediaContentsEntity.id.value,
            title = mediaContentsEntity.title,
            synopsis = mediaContentsEntity.synopsis,
            trailer = mediaContentsEntity.trailer,
            thumbnail = mediaContentsEntity.thumbnail,
            rate = mediaContentsEntity.rate,
            isSeries = mediaContentsEntity.isSeries,
            isDeleted = mediaContentsEntity.isDeleted,
            actorList = mediaContentsEntity.actorList.map { Actor.from(it) },
            genreList = mediaContentsEntity.genreList.map { Genre.from(it) },
            creatorList = mediaContentsEntity.creatorList.map { Creator.from(it) }
        )
    }
}

class MediaContentsEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<MediaContentsEntity>(MediaContentsTable)

    var title by MediaContentsTable.title
    var synopsis by MediaContentsTable.synopsis
    var trailer by MediaContentsTable.trailer
    var thumbnail by MediaContentsTable.thumbnail
    var rate by MediaContentsTable.rate
    var isSeries by MediaContentsTable.isSeries
    var isDeleted by MediaContentsTable.isDeleted
    var actorList by ActorEntity via MediaAllSeriesActorTable
    var creatorList by CreatorEntity via MediaAllSeriesCreatorTable
    var genreList by GenreEntity via MediaAllSeriesGenreTable
}

object MediaAllSeriesActorTable : Table() {
    val mediaAllSeries = reference("media_group", MediaContentsTable)
    val actor = reference("actor", ActorTable)
    override val primaryKey = PrimaryKey(this.mediaAllSeries, this.actor, name = "PK_MediaAllSeriesActor_mg_act")
}

object MediaAllSeriesCreatorTable : Table() {
    val mediaAllSeries = reference("media_group", MediaContentsTable)
    val creator = reference("creator", CreatorTable)
    override val primaryKey = PrimaryKey(this.mediaAllSeries, this.creator, name = "PK_MediaAllSeriesDirector_mg_crt")
}

object MediaAllSeriesGenreTable : Table() {
    val mediaAllSeries = reference("media_group", MediaContentsTable)
    val genre = reference("genre", GenreTable)
    override val primaryKey = PrimaryKey(this.mediaAllSeries, this.genre, name = "PK_MediaAllSeriesGenre_mg_gen")
}