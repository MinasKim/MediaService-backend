package com.mediaservice.application.dto.user

import javax.validation.constraints.NotBlank

data class ProfileUpdateRequestDto(
    @field:NotBlank(message = "NAME IS NOT BLANK")
    val name: String,
    @field:NotBlank(message = "RATE IS NOT BLANK")
    val rate: String,
    @field:NotBlank(message = "PROFILE IMAGE IS NOT BLANK")
    val mainImage: String,
)
