package com.mediaservice.application.dto.media

import javax.validation.constraints.NotBlank

data class MediaContentsCreateRequestDto(
    @field:NotBlank(message = "TITLE IS NOT BLANK")
    val title: String,
    @field:NotBlank(message = "SYNOPSIS IS NOT BLANK")
    val synopsis: String,
    @field:NotBlank(message = "TRAILER IS NOT BLANK")
    val trailer: String,
    @field:NotBlank(message = "THUMBNAIL IS NOT BLANK")
    val thumbnail: String,
    @field:NotBlank(message = "RATE IS NOT BLANK")
    val rate: String,
    val isSeries: Boolean,
    @field:NotBlank(message = "MEDIA SERIES TITLE IS NOT BLANK")
    val mediaSeriesTitle: String,
    val genreList: List<GenreCreateRequestDto>?,
    val actorList: List<ActorCreateRequestDto>?,
    val creatorList: List<CreatorCreateRequestDto>?
)
