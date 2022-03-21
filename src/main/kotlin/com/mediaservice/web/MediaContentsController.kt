package com.mediaservice.web

import com.mediaservice.application.MediaContentsService
import com.mediaservice.application.dto.media.MediaContentsActorRequestDto
import com.mediaservice.application.dto.media.MediaContentsCreateRequestDto
import com.mediaservice.application.dto.media.MediaContentsCreatorRequestDto
import com.mediaservice.application.dto.media.MediaContentsGenreRequestDto
import com.mediaservice.application.dto.media.MediaContentsResponseDto
import com.mediaservice.application.dto.media.MediaContentsUpdateRequestDto
import com.mediaservice.application.dto.media.MediaSeriesCreateRequestDto
import com.mediaservice.application.dto.media.MediaSeriesResponseDto
import com.mediaservice.application.dto.media.MediaSeriesUpdateRequestDto
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
@RequestMapping("/api/v1/media-contents")
class MediaContentsController(private val mediaContentsService: MediaContentsService) {
    @GetMapping("/series/{id}")
    fun findMediaSeriesById(
        @AuthenticationPrincipal userId: String,
        @RequestHeader(value = "profileId") profileId: String,
        @PathVariable id: UUID
    ): MediaSeriesResponseDto {
        return this.mediaContentsService.findMediaSeriesById(
            UUID.fromString(userId),
            UUID.fromString(profileId),
            id
        )
    }

    @PostMapping("/{id}/series")
    fun createMediaSeries(
        @PathVariable id: UUID,
        @RequestBody @Valid mediaSeriesCreateRequestDto: MediaSeriesCreateRequestDto
    ): MediaSeriesResponseDto {
        return this.mediaContentsService.createMediaSeries(
            id,
            mediaSeriesCreateRequestDto
        )
    }

    @PutMapping("/series/{id}")
    fun updateMediaSeries(
        @PathVariable id: UUID,
        @RequestBody @Valid mediaSeriesUpdateRequestDto: MediaSeriesUpdateRequestDto
    ): MediaSeriesResponseDto {
        return this.mediaContentsService.updateMediaSeries(id, mediaSeriesUpdateRequestDto)
    }

    @DeleteMapping("/series/{id}")
    fun deleteMediaSeriesById(
        @PathVariable id: UUID
    ): MediaSeriesResponseDto {
        return this.mediaContentsService.deleteMediaSeriesById(id)
    }

    @GetMapping("/{id}")
    fun findMediaContentsById(
        @AuthenticationPrincipal userId: String,
        @RequestHeader(value = "profileId") profileId: String,
        @PathVariable id: UUID
    ): MediaContentsResponseDto {
        return this.mediaContentsService.findMediaContentsById(
            UUID.fromString(userId),
            UUID.fromString(profileId),
            id
        )
    }

    @PostMapping
    fun createMediaContents(
        @RequestBody @Valid mediaContentsCreateRequestDto: MediaContentsCreateRequestDto
    ): MediaContentsResponseDto {
        return this.mediaContentsService.createMediaContents(
            mediaContentsCreateRequestDto
        )
    }

    @PutMapping("/{id}")
    fun updateMediaContents(
        @PathVariable id: UUID,
        @RequestBody @Valid mediaContentsUpdateRequestDto: MediaContentsUpdateRequestDto
    ): MediaContentsResponseDto {
        return this.mediaContentsService.updateMediaContents(id, mediaContentsUpdateRequestDto)
    }

    @DeleteMapping("/{id}")
    fun deleteMediaContentsById(
        @PathVariable id: UUID
    ): MediaContentsResponseDto {
        return this.mediaContentsService.deleteMediaContentsById(id)
    }

    @PostMapping("/{id}/actor")
    fun createMediaContentsActor(
        @PathVariable id: UUID,
        @RequestBody @Valid mediaContentsActorRequestDto: MediaContentsActorRequestDto
    ): MediaContentsResponseDto {
        return this.mediaContentsService.createMediaContentsActor(id, mediaContentsActorRequestDto)
    }

    @DeleteMapping("/{id}/actor")
    fun deleteMediaContentsActor(
        @PathVariable id: UUID,
        @RequestBody @Valid mediaContentsActorRequestDto: MediaContentsActorRequestDto
    ): MediaContentsResponseDto {
        return this.mediaContentsService.deleteMediaContentsActor(id, mediaContentsActorRequestDto)
    }

    @PostMapping("/{id}/creator")
    fun createMediaContentsCreator(
        @PathVariable id: UUID,
        @RequestBody @Valid mediaContentsCreatorRequestDto: MediaContentsCreatorRequestDto
    ): MediaContentsResponseDto {
        return this.mediaContentsService.createMediaContentsCreator(id, mediaContentsCreatorRequestDto)
    }

    @DeleteMapping("/{id}/creator")
    fun deleteMediaContentsCreator(
        @PathVariable id: UUID,
        @RequestBody @Valid mediaContentsCreatorRequestDto: MediaContentsCreatorRequestDto
    ): MediaContentsResponseDto {
        return this.mediaContentsService.deleteMediaContentsCreator(id, mediaContentsCreatorRequestDto)
    }

    @PostMapping("/{id}/genre")
    fun createMediaContentsGenre(
        @PathVariable id: UUID,
        @RequestBody @Valid mediaContentsGenreRequestDto: MediaContentsGenreRequestDto
    ): MediaContentsResponseDto {
        return this.mediaContentsService.createMediaContentsGenre(id, mediaContentsGenreRequestDto)
    }

    @DeleteMapping("/{id}/genre")
    fun deleteMediaContentsGenre(
        @PathVariable id: UUID,
        @RequestBody @Valid mediaContentsGenreRequestDto: MediaContentsGenreRequestDto
    ): MediaContentsResponseDto {
        return this.mediaContentsService.deleteMediaContentsGenre(id, mediaContentsGenreRequestDto)
    }
}
