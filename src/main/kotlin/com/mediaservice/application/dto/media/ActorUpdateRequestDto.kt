package com.mediaservice.application.dto.media

import javax.validation.constraints.NotBlank

data class ActorUpdateRequestDto(
    @field:NotBlank(message = "NAME IS NOT BLANK")
    val name: String
)
