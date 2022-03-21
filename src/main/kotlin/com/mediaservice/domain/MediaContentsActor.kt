package com.mediaservice.domain

import org.jetbrains.exposed.sql.Table

object MediaContentsActorTable : Table(name = "TB_MEDIA_CONTENTS_ACTOR") {
    val mediaContents = reference("media_contents", MediaContentsTable)
    val actor = reference("actor", ActorTable)
    override val primaryKey = PrimaryKey(this.mediaContents, this.actor, name = "PK_MediaAllSeriesActor_mg_act")
}

class MediaContentsActor(
    var mediaContents: MediaContents,
    var actor: Actor
) {
    companion object {
        const val DOMAIN = "MEDIA CONTENTS - ACTOR"

        fun of(mediaContents: MediaContents, actor: Actor) = MediaContentsActor(
            mediaContents = mediaContents,
            actor = actor
        )
    }
}
