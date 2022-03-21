package com.mediaservice.application

import com.mediaservice.application.dto.media.MediaContentsActorRequestDto
import com.mediaservice.application.dto.media.MediaContentsCreateRequestDto
import com.mediaservice.application.dto.media.MediaContentsCreatorRequestDto
import com.mediaservice.application.dto.media.MediaContentsGenreRequestDto
import com.mediaservice.application.dto.media.MediaContentsResponseDto
import com.mediaservice.application.dto.media.MediaContentsUpdateRequestDto
import com.mediaservice.application.dto.media.MediaSeriesCreateRequestDto
import com.mediaservice.application.dto.media.MediaSeriesResponseDto
import com.mediaservice.application.dto.media.MediaSeriesUpdateRequestDto
import com.mediaservice.application.validator.IdEqualValidator
import com.mediaservice.application.validator.IsDeletedValidator
import com.mediaservice.application.validator.RateMatchValidator
import com.mediaservice.application.validator.Validator
import com.mediaservice.domain.Like
import com.mediaservice.domain.Media
import com.mediaservice.domain.MediaContents
import com.mediaservice.domain.MediaContentsActor
import com.mediaservice.domain.MediaContentsCreator
import com.mediaservice.domain.MediaContentsGenre
import com.mediaservice.domain.MediaSeries
import com.mediaservice.domain.Profile
import com.mediaservice.domain.repository.ActorRepository
import com.mediaservice.domain.repository.CreatorRepository
import com.mediaservice.domain.repository.GenreRepository
import com.mediaservice.domain.repository.LikeRepository
import com.mediaservice.domain.repository.MediaContentsActorRepository
import com.mediaservice.domain.repository.MediaContentsCreatorRepository
import com.mediaservice.domain.repository.MediaContentsGenreRepository
import com.mediaservice.domain.repository.MediaContentsRepository
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
class MediaContentsService(
    private val mediaRepository: MediaRepository,
    private val mediaSeriesRepository: MediaSeriesRepository,
    private val mediaContentsRepository: MediaContentsRepository,
    private val profileRepository: ProfileRepository,
    private val actorRepository: ActorRepository,
    private val creatorRepository: CreatorRepository,
    private val genreRepository: GenreRepository,
    private val mediaContentsActorRepository: MediaContentsActorRepository,
    private val mediaContentsCreatorRepository: MediaContentsCreatorRepository,
    private val mediaContentsGenreRepository: MediaContentsGenreRepository,
    private val likeRepository: LikeRepository
) {
    @Transactional(readOnly = true)
    fun findMediaSeriesById(
        userId: UUID,
        profileId: UUID,
        id: UUID
    ): MediaSeriesResponseDto {
        val profile = this.profileRepository.findById(profileId)
            ?: throw BadRequestException(
                ErrorCode.ROW_DOES_NOT_EXIST,
                "NO SUCH PROFILE $profileId"
            )

        val validator = IsDeletedValidator(profile.isDeleted, Profile.DOMAIN)
        validator.linkWith(IdEqualValidator(userId, profile.user.id!!))
        validator.validate()

        return MediaSeriesResponseDto.from(
            this.mediaSeriesRepository.findById(id) ?: throw BadRequestException(
                ErrorCode.ROW_DOES_NOT_EXIST,
                "NO SUCH MEDIA SERIES $id"
            )
        )
    }

    @Transactional
    fun createMediaSeries(
        id: UUID,
        mediaSeriesCreateRequestDto: MediaSeriesCreateRequestDto
    ): MediaSeriesResponseDto {
        val mediaContents = this.mediaContentsRepository.findById(id)
            ?: throw BadRequestException(
                ErrorCode.ROW_DOES_NOT_EXIST,
                "NO SUCH MEDIA CONTENTS $id"
            )

        return MediaSeriesResponseDto.from(
            this.mediaSeriesRepository.save(
                MediaSeries.of(
                    mediaSeriesCreateRequestDto.title,
                    mediaSeriesCreateRequestDto.order,
                    mediaContents
                )
            )
        )
    }

    @Transactional
    fun updateMediaSeries(
        id: UUID,
        mediaSeriesUpdateRequestDto: MediaSeriesUpdateRequestDto
    ): MediaSeriesResponseDto {
        val mediaSeriesForUpdate = this.mediaSeriesRepository.findById(id) ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST, "NO SUCH MEDIA SERIES $id"
        )

        val validator: Validator = IsDeletedValidator(mediaSeriesForUpdate.isDeleted, MediaSeries.DOMAIN)
        validator.validate()

        mediaSeriesForUpdate.update(
            mediaSeriesUpdateRequestDto.title,
            mediaSeriesUpdateRequestDto.order
        )

        return MediaSeriesResponseDto.from(
            this.mediaSeriesRepository.update(mediaSeriesForUpdate) ?: throw InternalServerException(
                ErrorCode.INTERNAL_SERVER, "MEDIA SERIES IS CHECKED, BUT EXCEPTION OCCURS"
            )
        )
    }

    @Transactional
    fun deleteMediaSeriesById(id: UUID): MediaSeriesResponseDto {
        var mediaSeriesForDelete = this.mediaSeriesRepository.findById(id) ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST, "NO SUCH MEDIA SERIES $id"
        )

        val validator: Validator = IsDeletedValidator(mediaSeriesForDelete.isDeleted, MediaSeries.DOMAIN)
        validator.validate()

        mediaSeriesForDelete = this.mediaSeriesRepository.deleteById(id) ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST, "NO SUCH MEDIA SERIES $id"
        )

        val mediaList: List<Media> = this.mediaRepository.findByMediaSeriesId(mediaSeriesForDelete.id!!)

        mediaList.stream().forEach {
            this.mediaRepository.deleteById(it.id!!)
        }

        return MediaSeriesResponseDto.from(mediaSeriesForDelete)
    }

    @Transactional(readOnly = true)
    fun findMediaContentsById(
        userId: UUID,
        profileId: UUID,
        mediaContentsId: UUID
    ): MediaContentsResponseDto {
        val profile = this.profileRepository.findById(profileId)
            ?: throw BadRequestException(
                ErrorCode.ROW_DOES_NOT_EXIST,
                "NO SUCH PROFILE $profileId"
            )

        val validator = IsDeletedValidator(profile.isDeleted, Profile.DOMAIN)

        val mediaContents = this.mediaContentsRepository.findById(mediaContentsId)
            ?: throw BadRequestException(
                ErrorCode.ROW_DOES_NOT_EXIST,
                "NO SUCH MEDIA CONTENTS $mediaContentsId"
            )

        validator
            .linkWith(RateMatchValidator(profile.rate, mediaContents.rate))
            .linkWith(IdEqualValidator(userId, profile.user.id!!))
        validator.validate()

        val mediaSeriesList = this.mediaSeriesRepository.findByMediaContentsId(mediaContents.id!!)

        return MediaContentsResponseDto.from(
            mediaContents,
            mediaSeriesList,
            mediaSeriesList.stream().findFirst().map {
                this.mediaRepository.findByMediaSeriesId(it.id!!)
            }.orElse(listOf()),
            this.likeRepository.isExist(Like.of(profile, mediaContents))
        )
    }

    @Transactional
    fun createMediaContents(
        mediaContentsCreateRequestDto: MediaContentsCreateRequestDto
    ): MediaContentsResponseDto {
        val mediaContents = this.mediaContentsRepository.save(
            MediaContents.of(
                mediaContentsCreateRequestDto.title,
                mediaContentsCreateRequestDto.synopsis,
                mediaContentsCreateRequestDto.trailer,
                mediaContentsCreateRequestDto.thumbnail,
                mediaContentsCreateRequestDto.rate,
                mediaContentsCreateRequestDto.isSeries
            )
        )

        val mediaSeries = this.mediaSeriesRepository.save(
            MediaSeries.of(
                mediaContentsCreateRequestDto.mediaSeriesTitle,
                1,
                mediaContents
            )
        )

        return MediaContentsResponseDto.from(
            mediaContents,
            listOf(mediaSeries),
            listOf(),
            false
        )
    }

    @Transactional
    fun updateMediaContents(
        id: UUID,
        mediaContentsUpdateRequestDto: MediaContentsUpdateRequestDto
    ): MediaContentsResponseDto {
        val mediaContentsForUpdate = this.mediaContentsRepository.findById(id) ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST, "NO SUCH MEDIA CONTENTS $id"
        )

        val validator: Validator = IsDeletedValidator(mediaContentsForUpdate.isDeleted, MediaContents.DOMAIN)
        validator.validate()

        mediaContentsForUpdate.update(
            mediaContentsUpdateRequestDto.title,
            mediaContentsUpdateRequestDto.synopsis,
            mediaContentsUpdateRequestDto.trailer,
            mediaContentsUpdateRequestDto.thumbnail,
            mediaContentsUpdateRequestDto.rate,
            mediaContentsUpdateRequestDto.isSeries
        )

        return MediaContentsResponseDto.from(
            this.mediaContentsRepository.update(mediaContentsForUpdate) ?: throw InternalServerException(
                ErrorCode.INTERNAL_SERVER, "MEDIA CONTENTS IS CHECKED, BUT EXCEPTION OCCURS"
            ),
            listOf(),
            listOf(),
            false
        )
    }

    @Transactional
    fun deleteMediaContentsById(id: UUID): MediaContentsResponseDto {
        var mediaContentsForDelete = this.mediaContentsRepository.findById(id) ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST,
            "NO SUCH MEDIA CONTENTS $id"
        )

        val validator: Validator = IsDeletedValidator(mediaContentsForDelete.isDeleted, MediaSeries.DOMAIN)
        validator.validate()

        mediaContentsForDelete = this.mediaContentsRepository.deleteById(id) ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST,
            "NO SUCH MEDIA CONTENTS $id"
        )

        val mediaSeriesList: List<MediaSeries> = this.mediaSeriesRepository.findByMediaContentsId(
            mediaContentsForDelete.id!!
        )

        mediaSeriesList.stream().forEach { mediaSeries ->
            this.mediaSeriesRepository.deleteById(mediaSeries.id!!)

            val mediaList: List<Media> = this.mediaRepository.findByMediaSeriesId(mediaSeries.id!!)

            mediaList.stream().forEach { media ->
                this.mediaRepository.deleteById(media.id!!)
            }
        }

        return MediaContentsResponseDto.from(
            mediaContentsForDelete,
            listOf(),
            listOf(),
            false
        )
    }

    @Transactional
    fun createMediaContentsActor(
        id: UUID,
        mediaContentsActorRequestDto: MediaContentsActorRequestDto
    ): MediaContentsResponseDto {
        val mediaContents = mediaContentsRepository.findById(id) ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST,
            "NO SUCH MEDIA CONTENTS $id"
        )

        mediaContentsActorRequestDto.actorList.forEach {
            val actor = actorRepository.findById(it) ?: throw BadRequestException(
                ErrorCode.ROW_DOES_NOT_EXIST,
                "NO SUCH ACTOR $it"
            )
            mediaContentsActorRepository.save(MediaContentsActor.of(mediaContents, actor))
        }

        return MediaContentsResponseDto.from(
            mediaContents,
            listOf(),
            listOf(),
            false
        )
    }

    @Transactional
    fun deleteMediaContentsActor(
        id: UUID,
        mediaContentsActorRequestDto: MediaContentsActorRequestDto
    ): MediaContentsResponseDto {
        val mediaContents = mediaContentsRepository.findById(id) ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST,
            "NO SUCH MEDIA CONTENTS $id"
        )

        mediaContentsActorRequestDto.actorList.forEach {
            val actor = actorRepository.findById(it) ?: throw BadRequestException(
                ErrorCode.ROW_DOES_NOT_EXIST,
                "NO SUCH ACTOR $it"
            )

            mediaContentsActorRepository.delete(MediaContentsActor.of(mediaContents, actor))
        }

        return MediaContentsResponseDto.from(
            mediaContents,
            listOf(),
            listOf(),
            false
        )
    }

    @Transactional
    fun createMediaContentsCreator(
        id: UUID,
        mediaContentsCreatorRequestDto: MediaContentsCreatorRequestDto
    ): MediaContentsResponseDto {
        val mediaContents = mediaContentsRepository.findById(id) ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST,
            "NO SUCH MEDIA CONTENTS $id"
        )

        mediaContentsCreatorRequestDto.creatorList.forEach {
            val creator = creatorRepository.findById(it) ?: throw BadRequestException(
                ErrorCode.ROW_DOES_NOT_EXIST,
                "NO SUCH CREATOR $it"
            )

            mediaContentsCreatorRepository.save(MediaContentsCreator.of(mediaContents, creator))
        }

        return MediaContentsResponseDto.from(
            mediaContents,
            listOf(),
            listOf(),
            false
        )
    }

    @Transactional
    fun deleteMediaContentsCreator(
        id: UUID,
        mediaContentsCreatorRequestDto: MediaContentsCreatorRequestDto
    ): MediaContentsResponseDto {
        val mediaContents = mediaContentsRepository.findById(id) ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST,
            "NO SUCH MEDIA CONTENTS $id"
        )

        mediaContentsCreatorRequestDto.creatorList.forEach {
            val creator = creatorRepository.findById(it) ?: throw BadRequestException(
                ErrorCode.ROW_DOES_NOT_EXIST,
                "NO SUCH CREATOR $it"
            )

            mediaContentsCreatorRepository.delete(MediaContentsCreator.of(mediaContents, creator))
        }

        return MediaContentsResponseDto.from(
            mediaContents,
            listOf(),
            listOf(),
            false
        )
    }

    @Transactional
    fun createMediaContentsGenre(
        id: UUID,
        mediaContentsGenreRequestDto: MediaContentsGenreRequestDto
    ): MediaContentsResponseDto {
        val mediaContents = mediaContentsRepository.findById(id) ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST,
            "NO SUCH MEDIA CONTENTS $id"
        )

        mediaContentsGenreRequestDto.genreList.forEach {
            val genre = genreRepository.findById(it) ?: throw BadRequestException(
                ErrorCode.ROW_DOES_NOT_EXIST,
                "NO SUCH GENRE $it"
            )

            mediaContentsGenreRepository.save(MediaContentsGenre.of(mediaContents, genre))
        }

        return MediaContentsResponseDto.from(
            mediaContents,
            listOf(),
            listOf(),
            false
        )
    }

    @Transactional
    fun deleteMediaContentsGenre(
        id: UUID,
        mediaContentsGenreRequestDto: MediaContentsGenreRequestDto
    ): MediaContentsResponseDto {
        val mediaContents = mediaContentsRepository.findById(id) ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST,
            "NO SUCH MEDIA CONTENTS $id"
        )

        mediaContentsGenreRequestDto.genreList.forEach {
            val genre = genreRepository.findById(it) ?: throw BadRequestException(
                ErrorCode.ROW_DOES_NOT_EXIST,
                "NO SUCH GENRE $it"
            )

            mediaContentsGenreRepository.delete(MediaContentsGenre.of(mediaContents, genre))
        }

        return MediaContentsResponseDto.from(
            mediaContents,
            listOf(),
            listOf(),
            false
        )
    }
}
