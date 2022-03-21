package com.mediaservice.application

import com.mediaservice.application.dto.media.MediaCreateRequestDto
import com.mediaservice.application.dto.media.MediaResponseDto
import com.mediaservice.application.dto.media.MediaUpdateRequestDto
import com.mediaservice.application.validator.IdEqualValidator
import com.mediaservice.application.validator.IsDeletedValidator
import com.mediaservice.application.validator.Validator
import com.mediaservice.domain.Media
import com.mediaservice.domain.Profile
import com.mediaservice.domain.repository.MediaRepository
import com.mediaservice.domain.repository.MediaSeriesRepository
import com.mediaservice.domain.repository.ProfileRepository
import com.mediaservice.exception.BadRequestException
import com.mediaservice.exception.ErrorCode
import com.mediaservice.exception.InternalServerException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class MediaService(
    private val profileRepository: ProfileRepository,
    private val mediaRepository: MediaRepository,
    private val mediaSeriesRepository: MediaSeriesRepository
) {
    @Transactional(readOnly = true)
    fun findById(
        userId: UUID,
        profileId: UUID,
        id: UUID
    ): MediaResponseDto {
        val profile = this.profileRepository.findById(profileId)
            ?: throw BadRequestException(
                ErrorCode.ROW_DOES_NOT_EXIST,
                "NO SUCH PROFILE $profileId"
            )

        val validator = IsDeletedValidator(profile.isDeleted, Profile.DOMAIN)
        validator.linkWith(IdEqualValidator(userId, profile.user.id!!))
        validator.validate()

        return MediaResponseDto.from(
            this.mediaRepository.findById(id) ?: throw BadRequestException(
                ErrorCode.ROW_DOES_NOT_EXIST,
                "NO SUCH MEDIA $id"
            )
        )
    }

    @Transactional(readOnly = true)
    fun findByMediaSeries(
        userId: UUID,
        profileId: UUID,
        id: UUID
    ): List<MediaResponseDto> {
        val profile = this.profileRepository.findById(profileId)
            ?: throw BadRequestException(
                ErrorCode.ROW_DOES_NOT_EXIST,
                "NO SUCH PROFILE $profileId"
            )

        val validator = IsDeletedValidator(profile.isDeleted, Profile.DOMAIN)
        validator.linkWith(IdEqualValidator(userId, profile.user.id!!))
        validator.validate()

        return this.mediaRepository.findByMediaSeriesId(id).map {
            MediaResponseDto.from(it)
        }
    }

    @Transactional
    fun createMedia(
        id: UUID,
        mediaCreateRequestDto: MediaCreateRequestDto
    ): MediaResponseDto {
        val mediaSeries = this.mediaSeriesRepository.findById(id)
            ?: throw BadRequestException(
                ErrorCode.ROW_DOES_NOT_EXIST,
                "NO SUCH MEDIA SERIES $id"
            )

        return MediaResponseDto.from(
            this.mediaRepository.save(
                Media.of(
                    mediaCreateRequestDto.name,
                    mediaCreateRequestDto.synopsis,
                    mediaCreateRequestDto.order,
                    mediaCreateRequestDto.url,
                    mediaCreateRequestDto.thumbnail,
                    mediaCreateRequestDto.runningTime,
                    mediaSeries
                )
            )
        )
    }

    @Transactional
    fun update(
        id: UUID,
        mediaUpdateRequestDto: MediaUpdateRequestDto
    ): MediaResponseDto {
        val mediaForUpdate = this.mediaRepository.findById(id) ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST, "NO SUCH MEDIA $id"
        )

        val validator: Validator = IsDeletedValidator(mediaForUpdate.isDeleted, Media.DOMAIN)
        validator.validate()

        mediaForUpdate.update(
            mediaUpdateRequestDto.name,
            mediaUpdateRequestDto.synopsis,
            mediaUpdateRequestDto.order,
            mediaUpdateRequestDto.url,
            mediaUpdateRequestDto.thumbnail,
            mediaUpdateRequestDto.runningTime
        )

        return MediaResponseDto.from(
            this.mediaRepository.update(mediaForUpdate) ?: throw InternalServerException(
                ErrorCode.INTERNAL_SERVER,
                "MEDIA IS CHECKED, BUT EXCEPTION OCCURS"
            )
        )
    }

    @Transactional
    fun deleteById(
        id: UUID
    ): MediaResponseDto {
        val mediaForDelete = this.mediaRepository.findById(id) ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST,
            "NO SUCH MEDIA $id"
        )

        val validator: Validator = IsDeletedValidator(mediaForDelete.isDeleted, Media.DOMAIN)
        validator.validate()

        return MediaResponseDto.from(
            this.mediaRepository.deleteById(id) ?: throw BadRequestException(
                ErrorCode.INTERNAL_SERVER,
                "MEDIA IS CHECKED, BUT EXCEPTION OCCURS"
            )
        )
    }
}
