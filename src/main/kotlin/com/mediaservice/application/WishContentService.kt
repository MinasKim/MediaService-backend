package com.mediaservice.application

import com.mediaservice.application.dto.media.WishContentRequestDto
import com.mediaservice.application.dto.media.WishContentResponseDto
import com.mediaservice.domain.WishContent
import com.mediaservice.domain.repository.MediaContentsRepository
import com.mediaservice.domain.repository.ProfileRepository
import com.mediaservice.domain.repository.WishContentRepository
import com.mediaservice.exception.BadRequestException
import com.mediaservice.exception.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class WishContentService(
    private val wishContentRepository: WishContentRepository,
    private val profileRepository: ProfileRepository,
    private val mediaContentsRepository: MediaContentsRepository
) {

    @Transactional(readOnly = true)
    fun findByProfileId(id: UUID): List<WishContentResponseDto> {
        return this.wishContentRepository.findByProfileId(id)
            .map { wishContent -> WishContentResponseDto.from(wishContent) }
    }

    @Transactional
    fun createWishContent(wishContentRequestDto: WishContentRequestDto, profileId: UUID): WishContentResponseDto {
        val mediaContentsId = wishContentRequestDto.mediaContentsId
        val mediaContents = mediaContentsRepository.findById(wishContentRequestDto.mediaContentsId)
            ?: throw BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "NO SUCH USER $mediaContentsId")
        val profile = profileRepository.findById(profileId)
            ?: throw BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "NO SUCH USER $profileId")

        if (wishContentRepository.existsByProfileIdAndMediaAllSeriesId(profileId, mediaContentsId))
            throw BadRequestException(ErrorCode.ROW_ALREADY_EXIST, "$mediaContentsId is Already Inserted in $profileId")

        return WishContentResponseDto.from(
            this.wishContentRepository.save(
                WishContent.of(
                    profile = profile,
                    mediaContents = mediaContents
                )
            )
        )
    }

    @Transactional
    fun deleteWishContent(wishContentRequestDto: WishContentRequestDto, profileId: UUID): List<WishContentResponseDto> {
        val mediaContentsId = wishContentRequestDto.mediaContentsId
        val mediaContents = mediaContentsRepository.findById(wishContentRequestDto.mediaContentsId)
            ?: throw BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "NO SUCH USER $mediaContentsId")
        val profile = profileRepository.findById(profileId)
            ?: throw BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "NO SUCH USER $profileId")

        return this.wishContentRepository.delete(
            WishContent.of(
                profile = profile,
                mediaContents = mediaContents
            )
        )?.map {
            WishContentResponseDto.from(it)
        }
    }
}
