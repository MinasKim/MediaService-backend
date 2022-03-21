package com.mediaservice.web

import com.mediaservice.application.UserService
import com.mediaservice.application.dto.user.PasswordUpdateRequestDto
import com.mediaservice.application.dto.user.ProfileResponseDto
import com.mediaservice.application.dto.user.UserResponseDto
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {
    @GetMapping("/email/is-duplicated")
    fun isDuplicatedByEmail(@RequestParam email: String): Boolean {
        return this.userService.isDuplicatedByEmail(email)
    }

    @PutMapping("/password")
    fun updatePassword(
        @AuthenticationPrincipal id: String,
        @RequestBody @Valid passwordUpdateRequestDto: PasswordUpdateRequestDto
    ): UserResponseDto {
        return this.userService.updatePassword(UUID.fromString(id), passwordUpdateRequestDto)
    }

    @GetMapping("/profiles")
    fun findProfiles(
        @AuthenticationPrincipal id: String
    ): List<ProfileResponseDto> {
        return this.userService.findProfiles(UUID.fromString(id))
    }
}
