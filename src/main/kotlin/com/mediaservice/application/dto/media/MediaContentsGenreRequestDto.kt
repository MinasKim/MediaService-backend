package com.mediaservice.application.dto.media

import java.util.UUID

data class MediaContentsGenreRequestDto(
    val genreList: List<UUID>
)
