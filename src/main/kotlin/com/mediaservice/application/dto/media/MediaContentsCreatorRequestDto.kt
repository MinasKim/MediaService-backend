package com.mediaservice.application.dto.media

import java.util.UUID

data class MediaContentsCreatorRequestDto(
    val creatorList: List<UUID>
)
