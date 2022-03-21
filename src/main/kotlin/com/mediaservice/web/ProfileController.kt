package com.mediaservice.web

import com.mediaservice.application.ProfileService
import com.mediaservice.application.dto.user.LikeRequestDto
import com.mediaservice.application.dto.user.LikeResponseDto
import com.mediaservice.application.dto.user.ProfileCreateRequestDto
import com.mediaservice.application.dto.user.ProfileResponseDto
import com.mediaservice.application.dto.user.ProfileUpdateRequestDto
import com.mediaservice.application.dto.user.SignInProfileResponseDto
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/profiles")
class ProfileController(private val profileService: ProfileService) {
    @GetMapping("/{id}")
    fun findById(@PathVariable id: UUID): ProfileResponseDto {
        return this.profileService.findById(id)
    }

    @PostMapping("/sign-in")
    fun findByUserId(@AuthenticationPrincipal id: String): List<SignInProfileResponseDto> {
        return this.profileService.findByUserId(UUID.fromString(id))
    }

    @PostMapping
    fun create(
        @AuthenticationPrincipal userId: String,
        @RequestBody @Valid profileCreateRequestDto: ProfileCreateRequestDto
    ): ProfileResponseDto? {
        return profileService.create(UUID.fromString(userId), profileCreateRequestDto)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @AuthenticationPrincipal userId: String,
        @PathVariable id: UUID
    ): ProfileResponseDto {
        return this.profileService.delete(UUID.fromString(userId), id)
    }

    @PutMapping("/{profileId}")
    fun update(
        @AuthenticationPrincipal userId: String,
        @PathVariable profileId: UUID,
        @RequestBody @Valid profileUpdateRequestDto: ProfileUpdateRequestDto
    ): ProfileResponseDto? {
        return this.profileService.update(UUID.fromString(userId), profileId, profileUpdateRequestDto)
    }

    @PostMapping("/like")
    fun createLike(@RequestBody likeRequestDto: LikeRequestDto): LikeResponseDto {
        return this.profileService.createLike(likeRequestDto)
    }

    @DeleteMapping("/like")
    fun deleteLike(@RequestBody likeRequestDto: LikeRequestDto): LikeResponseDto {
        return this.profileService.deleteLike(likeRequestDto)
    }
}
