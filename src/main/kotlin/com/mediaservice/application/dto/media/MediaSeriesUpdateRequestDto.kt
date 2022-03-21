package com.mediaservice.application.dto.media

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Positive

data class MediaSeriesUpdateRequestDto(
    @field:NotBlank(message = "TITLE IS NOT BLANK")
    val title: String,
    @field:Positive(message = "ORDER SHOULD BE POSITIVE")
    val order: Int
)
