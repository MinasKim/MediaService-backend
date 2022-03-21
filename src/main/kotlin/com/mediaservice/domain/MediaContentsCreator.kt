package com.mediaservice.domain

import org.jetbrains.exposed.sql.Table

object MediaContentsCreatorTable : Table(name = "TB_MEDIA_CONTENTS_CREATOR") {
    val mediaContents = reference("media_contents", MediaContentsTable)
    val creator = reference("creator", CreatorTable)
    override val primaryKey = PrimaryKey(this.mediaContents, this.creator, name = "PK_MediaAllSeriesDirector_mg_crt")
}

class MediaContentsCreator(
    var mediaContents: MediaContents,
    var creator: Creator
) {
    companion object {
        const val DOMAIN = "MEDIA CONTENTS - CREATOR"

        fun of(mediaContents: MediaContents, creator: Creator) = MediaContentsCreator(
            mediaContents = mediaContents,
            creator = creator
        )
    }
}
