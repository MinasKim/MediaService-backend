package com.mediaservice.application.dto.user

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class PasswordFindRequestDto(
    @field:Email(message = "IS NOT EMAIL FORMAT")
    @field:NotBlank(message = "EMAIL IS NOT BLANK")
    val email: String
)
