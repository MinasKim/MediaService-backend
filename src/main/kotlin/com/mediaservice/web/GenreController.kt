package com.mediaservice.web

import com.mediaservice.application.GenreService
import com.mediaservice.application.dto.media.GenreCreateRequestDto
import com.mediaservice.application.dto.media.GenreResponseDto
import com.mediaservice.application.dto.media.GenreUpdateRequestDto
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/genres")
class GenreController(private val genreService: GenreService) {
    @PostMapping("")
    fun create(
        @RequestBody @Valid genreCreateRequestDto: GenreCreateRequestDto
    ): GenreResponseDto {
        return this.genreService.create(genreCreateRequestDto)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid genreUpdateRequestDto: GenreUpdateRequestDto
    ): GenreResponseDto {
        return this.genreService.update(id, genreUpdateRequestDto)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: UUID
    ): GenreResponseDto {
        return this.genreService.delete(id)
    }

    @GetMapping
    fun findAll(
        @AuthenticationPrincipal userId: String,
        @RequestHeader(value = "profileId") profileId: String
    ): List<GenreResponseDto> {
        return this.genreService.findAll(
            UUID.fromString(userId),
            UUID.fromString(profileId)
        )
    }
}
