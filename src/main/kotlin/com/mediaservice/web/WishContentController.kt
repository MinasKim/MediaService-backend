package com.mediaservice.web

import com.mediaservice.application.WishContentService
import com.mediaservice.application.dto.media.WishContentRequestDto
import com.mediaservice.application.dto.media.WishContentResponseDto
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/wish-contents")
class WishContentController(private val wishContentService: WishContentService) {

    @GetMapping("/{profileId}")
    fun findWishContentByProfileId(@PathVariable profileId: UUID): List<WishContentResponseDto> {
        return this.wishContentService.findByProfileId(profileId)
    }
    @PostMapping("/{profileId}")
    fun createWishContent(@PathVariable profileId: UUID, @RequestBody wishContentRequestDto: WishContentRequestDto): WishContentResponseDto {
        return this.wishContentService.createWishContent(wishContentRequestDto, profileId)
    }
    @DeleteMapping("/{profileId}")
    fun deleteWishContent(@PathVariable profileId: UUID, @RequestBody wishContentRequestDto: WishContentRequestDto): List<WishContentResponseDto> {
        return this.wishContentService.deleteWishContent(wishContentRequestDto, profileId)
    }
}
