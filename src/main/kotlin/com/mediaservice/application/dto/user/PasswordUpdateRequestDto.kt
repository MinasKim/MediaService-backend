package com.mediaservice.application.dto.user

import javax.validation.constraints.NotBlank

data class PasswordUpdateRequestDto(
    @field:NotBlank(message = "PASSWORD IS NOT BLANK")
    val srcPassword: String,
    @field:NotBlank(message = "PASSWORD IS NOT BLANK")
    val dstPassword: String
)
