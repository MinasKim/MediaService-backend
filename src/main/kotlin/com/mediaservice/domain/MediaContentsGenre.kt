package com.mediaservice.domain

import org.jetbrains.exposed.sql.Table

object MediaContentsGenreTable : Table(name = "TB_MEDIA_CONTENTS_GENRE") {
    val mediaContents = reference("media_contents", MediaContentsTable)
    val genre = reference("genre", GenreTable)
    override val primaryKey = PrimaryKey(this.mediaContents, this.genre, name = "PK_MediaAllSeriesGenre_mg_gen")
}

class MediaContentsGenre(
    var mediaContents: MediaContents,
    var genre: Genre
) {
    companion object {
        const val DOMAIN = "MEDIA CONTENTS - GENRE"

        fun of(mediaContents: MediaContents, genre: Genre) = MediaContentsGenre(
            mediaContents = mediaContents,
            genre = genre
        )
    }
}
