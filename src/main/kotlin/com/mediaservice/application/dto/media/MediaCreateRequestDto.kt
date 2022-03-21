package com.mediaservice.application.dto.media

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Positive

data class MediaCreateRequestDto(
    @field:NotBlank(message = "NAME IS NOT BLANK")
    val name: String,
    @field:NotBlank(message = "SYNOPSIS IS NOT BLANK")
    val synopsis: String,
    @field:Positive(message = "ORDER SHOULD BE POSITIVE")
    val order: Int,
    @field:NotBlank(message = "URL IS NOT BLANK")
    val url: String,
    @field:NotBlank(message = "THUMBNAIL IS NOT BLANK")
    val thumbnail: String,
    @field:Positive(message = "RUNNING TIME SHOULD BE POSITIVE")
    val runningTime: Int
)
