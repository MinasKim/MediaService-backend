package com.mediaservice

import com.mediaservice.application.MediaContentsService
import com.mediaservice.application.dto.media.MediaContentsActorRequestDto
import com.mediaservice.application.dto.media.MediaContentsCreateRequestDto
import com.mediaservice.application.dto.media.MediaContentsCreatorRequestDto
import com.mediaservice.application.dto.media.MediaContentsGenreRequestDto
import com.mediaservice.application.dto.media.MediaContentsUpdateRequestDto
import com.mediaservice.application.dto.media.MediaSeriesCreateRequestDto
import com.mediaservice.application.dto.media.MediaSeriesUpdateRequestDto
import com.mediaservice.domain.Actor
import com.mediaservice.domain.Creator
import com.mediaservice.domain.Genre
import com.mediaservice.domain.Media
import com.mediaservice.domain.MediaContents
import com.mediaservice.domain.MediaSeries
import com.mediaservice.domain.Profile
import com.mediaservice.domain.Role
import com.mediaservice.domain.User
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
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class MediaContentsServiceTest {
    private var mediaRepository = mockk<MediaRepository>()
    private var mediaSeriesRepository = mockk<MediaSeriesRepository>()
    private var mediaContentsRepository = mockk<MediaContentsRepository>()
    private var profileRepository = mockk<ProfileRepository>()
    private var actorRepository = mockk<ActorRepository>()
    private var creatorRepository = mockk<CreatorRepository>()
    private var genreRepository = mockk<GenreRepository>()
    private var mediaContentsActorRepository = mockk<MediaContentsActorRepository>()
    private var mediaContentsCreatorRepository = mockk<MediaContentsCreatorRepository>()
    private var mediaContentsGenreRepository = mockk<MediaContentsGenreRepository>()
    private var likeRepository = mockk<LikeRepository>()
    private val mediaContentsService: MediaContentsService =
        MediaContentsService(
            this.mediaRepository, this.mediaSeriesRepository, this.mediaContentsRepository,
            this.profileRepository, this.actorRepository, this.creatorRepository, this.genreRepository,
            this.mediaContentsActorRepository, this.mediaContentsCreatorRepository,
            this.mediaContentsGenreRepository, this.likeRepository
        )
    private lateinit var media: Media
    private lateinit var mediaSeries: MediaSeries
    private lateinit var createdMediaSeries: MediaSeries
    private lateinit var mediaContents: MediaContents
    private lateinit var mediaContentsNoActor: MediaContents
    private lateinit var mediaContentsNoGenre: MediaContents
    private lateinit var mediaContentsNoCreator: MediaContents
    private lateinit var user: User
    private lateinit var profile: Profile
    private lateinit var actor: Actor
    private lateinit var creator: Creator
    private lateinit var genre: Genre
    private lateinit var deletedProfile: Profile
    private lateinit var lowRateProfile: Profile
    private lateinit var mediaId: UUID
    private lateinit var mediaSeriesId: UUID
    private lateinit var mediaContentsId: UUID
    private lateinit var userId: UUID
    private lateinit var profileId: UUID
    private lateinit var actorList: List<Actor>
    private lateinit var genreList: List<Genre>
    private lateinit var creatorList: List<Creator>
    private lateinit var actorId: UUID
    private lateinit var creatorId: UUID
    private lateinit var genreId: UUID

    @BeforeEach
    fun setup() {
        clearAllMocks()
        this.mediaId = UUID.randomUUID()
        this.mediaSeriesId = UUID.randomUUID()
        this.mediaContentsId = UUID.randomUUID()
        this.userId = UUID.randomUUID()
        this.profileId = UUID.randomUUID()
        this.actorId = UUID.randomUUID()
        this.creatorId = UUID.randomUUID()
        this.genreId = UUID.randomUUID()
        this.actorList = listOf(Actor(UUID.randomUUID(), "testActor", false))
        this.genreList = listOf(Genre(UUID.randomUUID(), "testGenre", false))
        this.creatorList = listOf(Creator(UUID.randomUUID(), "testCreator", false))
        this.user = User(userId, "test@gmail.com", "test123!@", Role.USER)
        this.profile = Profile(profileId, this.user, "test profile", "19+", "test.jpg", false)
        this.actor = Actor(actorId, "test Actor", false)
        this.creator = Creator(creatorId, "test Creator", false)
        this.genre = Genre(genreId, "test Genre", false)
        this.deletedProfile = Profile(profileId, this.user, "test profile", "19+", "test.jpg", true)
        this.lowRateProfile = Profile(profileId, this.user, "test profile", "15+", "test.jpg", false)
        this.mediaContents = MediaContents(
            mediaContentsId, "test title", "test synopsis", "test trailer",
            "test thumbnail url", "19+", true,
            isDeleted = false,
            actorList = this.actorList,
            genreList = this.genreList,
            creatorList = this.creatorList
        )
        this.mediaContentsNoActor = MediaContents(
            mediaContentsId, "test title", "test synopsis", "test trailer",
            "test thumbnail url", "19+", true,
            isDeleted = false,
            actorList = null,
            genreList = this.genreList,
            creatorList = this.creatorList
        )
        this.mediaContentsNoGenre = MediaContents(
            mediaContentsId, "test title", "test synopsis", "test trailer",
            "test thumbnail url", "19+", true,
            isDeleted = false,
            actorList = this.actorList,
            genreList = null,
            creatorList = this.creatorList
        )
        this.mediaContentsNoCreator = MediaContents(
            mediaContentsId, "test title", "test synopsis", "test trailer",
            "test thumbnail url", "19+", true,
            isDeleted = false,
            actorList = this.actorList,
            genreList = this.genreList,
            creatorList = null
        )
        this.mediaSeries = MediaSeries(
            mediaSeriesId, "season 1", 1, false, this.mediaContents
        )
        this.createdMediaSeries = MediaSeries(
            UUID.randomUUID(), "season 2", 2, false, this.mediaContents
        )
        this.media = Media(
            mediaId, "test video 1", "test synopsis", 1, "test url",
            "test thumbnail", 100, false, this.mediaSeries
        )
    }

    @Test
    fun successFindMediaSeriesById() {
        // given
        every { mediaSeriesRepository.findById(mediaSeriesId) } returns this.mediaSeries
        every { profileRepository.findById(profileId) } returns this.profile

        // when
        val mediaSeriesResponseDto = this.mediaContentsService.findMediaSeriesById(this.userId, this.profileId, this.mediaSeriesId)

        // then
        assertEquals(this.mediaSeries.title, mediaSeriesResponseDto.title)
    }

    @Test
    fun failFindMediaSeriesById() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            every { mediaSeriesRepository.findById(mediaSeriesId) } returns null
            every { profileRepository.findById(profileId) } returns this.profile

            // when
            this.mediaContentsService.findMediaSeriesById(this.userId, this.profileId, this.mediaSeriesId)
        }

        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun successCreateMediaSeries() {
        // given
        mockkObject(MediaSeries)
        val mediaSeriesCreateRequestDto = MediaSeriesCreateRequestDto("season 2", 2)

        every {
            MediaSeries.of(
                mediaSeriesCreateRequestDto.title,
                mediaSeriesCreateRequestDto.order,
                mediaContents
            )
        } returns this.createdMediaSeries
        every { mediaContentsRepository.findById(mediaContentsId) } returns this.mediaContents
        every { mediaSeriesRepository.save(createdMediaSeries) } returns this.createdMediaSeries

        // when
        val mediaSeriesResponseDto = mediaContentsService.createMediaSeries(mediaContentsId, mediaSeriesCreateRequestDto)

        // then
        assertEquals("season 2", mediaSeriesResponseDto.title)
    }

    @Test
    fun failCreateMediaSeries_NoMediaContents() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val mediaSeriesCreateRequestDto = MediaSeriesCreateRequestDto("season 2", 2)

            every { mediaContentsRepository.findById(mediaContentsId) } returns null

            // when
            mediaContentsService.createMediaSeries(mediaContentsId, mediaSeriesCreateRequestDto)
        }

        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun successUpdateMediaSeries() {
        // given
        val mediaSeriesUpdateRequestDto = MediaSeriesUpdateRequestDto(
            "season 1", 1
        )

        every { mediaSeriesRepository.findById(mediaSeriesId) } returns this.mediaSeries
        every { mediaSeriesRepository.update(mediaSeries) } returns this.mediaSeries

        // when
        val mediaSeriesResponseDto = this.mediaContentsService.updateMediaSeries(
            this.mediaSeriesId,
            mediaSeriesUpdateRequestDto
        )

        // then
        assertEquals(this.mediaSeries.title, mediaSeriesResponseDto.title)
    }

    @Test
    fun failUpdateMediaSeries_CannotFindMediaSeries() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val mediaSeriesUpdateRequestDto = MediaSeriesUpdateRequestDto(
                "season 1", 1
            )

            every { mediaSeriesRepository.findById(mediaSeriesId) } returns null

            // when
            this.mediaContentsService.updateMediaSeries(this.mediaSeriesId, mediaSeriesUpdateRequestDto)
        }

        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failUpdateMediaSeries_AlreadyDeletedMediaSeries() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val mediaSeriesUpdateRequestDto = MediaSeriesUpdateRequestDto(
                "season 1", 1
            )

            this.mediaSeries.isDeleted = true
            every { mediaSeriesRepository.findById(mediaSeriesId) } returns this.mediaSeries

            // when
            this.mediaContentsService.updateMediaSeries(this.mediaSeriesId, mediaSeriesUpdateRequestDto)
        }

        // then
        assertEquals(ErrorCode.ROW_ALREADY_DELETED, exception.errorCode)
    }

    @Test
    fun successDeleteMediaSeriesById() {
        // given
        every { mediaSeriesRepository.findById(mediaSeriesId) } returns this.mediaSeries
        every { mediaSeriesRepository.deleteById(mediaSeriesId) } returns this.mediaSeries
        every { mediaRepository.findByMediaSeriesId(mediaSeriesId) } returns listOf(this.media)
        every { mediaRepository.deleteById(mediaId) } returns this.media

        // when
        val mediaSeriesResponseDto = this.mediaContentsService.deleteMediaSeriesById(this.mediaSeriesId)

        // then
        assertEquals(this.mediaSeries.title, mediaSeriesResponseDto.title)
    }

    @Test
    fun failDeleteMediaSeriesById_CannotFindMediaSeries() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            every { mediaSeriesRepository.findById(mediaSeriesId) } returns null

            // when
            this.mediaContentsService.deleteMediaSeriesById(this.mediaSeriesId)
        }

        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failDeleteMediaSeriesById_AlreadyDeletedMediaSeries() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            this.mediaSeries.isDeleted = true
            every { mediaSeriesRepository.findById(mediaSeriesId) } returns this.mediaSeries

            // when
            this.mediaContentsService.deleteMediaSeriesById(this.mediaSeriesId)
        }

        // then
        assertEquals(ErrorCode.ROW_ALREADY_DELETED, exception.errorCode)
    }

    @Test
    fun successFindMediaContentsById() {
        // given
        every {
            profileRepository.findById(profileId)
        } returns this.profile
        every {
            mediaContentsRepository.findById(mediaContentsId)
        } returns this.mediaContents
        every {
            mediaSeriesRepository.findByMediaContentsId(any())
        } returns listOf(this.mediaSeries)
        every {
            mediaRepository.findByMediaSeriesId(any())
        } returns listOf(this.media)
        every {
            likeRepository.isExist(any())
        } returns false

        // when
        val mediaContentsResponseDto = mediaContentsService.findMediaContentsById(this.userId, this.profileId, this.mediaContentsId)

        // then
        assertEquals(this.mediaContentsId, mediaContentsResponseDto.id)
    }

    @Test
    fun failFindDetail_DeletedProfile() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            every {
                profileRepository.findById(profileId)
            } returns deletedProfile
            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns this.mediaContents

            // when
            mediaContentsService.findMediaContentsById(this.userId, this.profileId, this.mediaContentsId)
        }

        // then
        assertEquals(ErrorCode.ROW_ALREADY_DELETED, exception.errorCode)
    }

    @Test
    fun failFindDetail_NoMediaAllSeries() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            every {
                profileRepository.findById(profileId)
            } returns profile
            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns null

            // when
            mediaContentsService.findMediaContentsById(this.userId, this.profileId, this.mediaContentsId)
        }

        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failFindDetail_RateNotMatch() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            every {
                profileRepository.findById(profileId)
            } returns lowRateProfile
            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns this.mediaContents

            // when
            mediaContentsService.findMediaContentsById(this.userId, this.profileId, this.mediaContentsId)
        }
        assertEquals(ErrorCode.RATE_NOT_MATCHED, exception.errorCode)
    }

    @Test
    fun successCreateMediaContents() {
        // given
        mockkObject(MediaContents)
        mockkObject(MediaSeries)
        val mediaContentsCreateRequestDto =
            MediaContentsCreateRequestDto(
                "test title",
                "test synopsis",
                "test trailer",
                "test thumbnail url",
                "19+",
                true,
                "season 1",
                null,
                null,
                null
            )

        every {
            MediaContents.of(
                mediaContentsCreateRequestDto.title,
                mediaContentsCreateRequestDto.synopsis,
                mediaContentsCreateRequestDto.trailer,
                mediaContentsCreateRequestDto.thumbnail,
                mediaContentsCreateRequestDto.rate,
                mediaContentsCreateRequestDto.isSeries
            )
        } returns this.mediaContents
        every {
            MediaSeries.of(
                mediaContentsCreateRequestDto.mediaSeriesTitle,
                1,
                mediaContents
            )
        } returns this.mediaSeries
        every { mediaContentsRepository.save(mediaContents) } returns this.mediaContents
        every { mediaSeriesRepository.save(mediaSeries) } returns this.mediaSeries

        // when
        val mediaContentsResponseDto = mediaContentsService.createMediaContents(mediaContentsCreateRequestDto)

        // then
        assertEquals(this.mediaContentsId, mediaContentsResponseDto.id)
    }

    @Test
    fun successUpdateMediaContents() {
        // given
        val mediaContentsUpdateRequestDto = MediaContentsUpdateRequestDto(
            "test title", "test synopsis", "test trailer",
            "test thumbnail url", "19+", true
        )

        every { mediaContentsRepository.findById(mediaContentsId) } returns this.mediaContents
        every { mediaContentsRepository.update(mediaContents) } returns this.mediaContents

        // when
        val mediaContentsResponseDto = this.mediaContentsService.updateMediaContents(
            this.mediaContentsId,
            mediaContentsUpdateRequestDto
        )

        // then
        assertEquals(this.mediaContents.title, mediaContentsResponseDto.title)
    }

    @Test
    fun failUpdateMediaContents_CannotFindMediaContents() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val mediaContentsUpdateRequestDto = MediaContentsUpdateRequestDto(
                "test title", "test synopsis", "test trailer",
                "test thumbnail url", "19+", true
            )

            every { mediaContentsRepository.findById(mediaContentsId) } returns null

            // when
            this.mediaContentsService.updateMediaContents(this.mediaContentsId, mediaContentsUpdateRequestDto)
        }

        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failUpdateMediaContents_AlreadyDeletedMediaContents() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val mediaContentsUpdateRequestDto = MediaContentsUpdateRequestDto(
                "test title", "test synopsis", "test trailer",
                "test thumbnail url", "19+", true
            )

            this.mediaContents.isDeleted = true
            every { mediaContentsRepository.findById(mediaContentsId) } returns this.mediaContents

            // when
            this.mediaContentsService.updateMediaContents(this.mediaContentsId, mediaContentsUpdateRequestDto)
        }

        // then
        assertEquals(ErrorCode.ROW_ALREADY_DELETED, exception.errorCode)
    }

    @Test
    fun successDeleteMediaContentsById() {
        // given
        every {
            mediaContentsRepository.findById(mediaContentsId)
        } returns this.mediaContents
        every {
            mediaContentsRepository.deleteById(mediaContentsId)
        } returns this.mediaContents
        every {
            mediaSeriesRepository.findByMediaContentsId(mediaContentsId)
        } returns listOf(this.mediaSeries)
        every {
            mediaSeriesRepository.deleteById(mediaSeriesId)
        } returns this.mediaSeries
        every {
            mediaRepository.findByMediaSeriesId(mediaSeriesId)
        } returns listOf(this.media)
        every {
            mediaRepository.deleteById(mediaId)
        } returns this.media

        // when
        val mediaContentsResponseDto = mediaContentsService.deleteMediaContentsById(this.mediaContentsId)

        // then
        assertEquals(this.mediaContentsId, mediaContentsResponseDto.id)
    }

    @Test
    fun failDeleteMediaContentsById_CannotFindMediaContents() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns null

            // when
            mediaContentsService.deleteMediaContentsById(this.mediaContentsId)
        }

        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failDeleteMediaContentsById_AlreadyDeletedMediaContents() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            this.mediaContents.isDeleted = true
            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns this.mediaContents

            // when
            mediaContentsService.deleteMediaContentsById(this.mediaContentsId)
        }

        // then
        assertEquals(ErrorCode.ROW_ALREADY_DELETED, exception.errorCode)
    }

    @Test
    fun successCreateMediaContentsActor() {
        // given
        val mediaContentsActorRequestDto = MediaContentsActorRequestDto(listOf(actorId))

        every {
            mediaContentsRepository.findById(mediaContentsId)
        } returns this.mediaContents

        every {
            actorRepository.findById(actorId)
        } returns this.actor

        every {
            mediaContentsActorRepository.save(any())
        } returns Unit

        // when
        val mediaContentsResponseDto = mediaContentsService.createMediaContentsActor(
            mediaContentsId,
            mediaContentsActorRequestDto
        )

        // then
        assertEquals(this.mediaContentsId, mediaContentsResponseDto.id)
    }

    @Test
    fun failCreateMediaContentsActor_noMediaContents() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val mediaContentsActorRequestDto = MediaContentsActorRequestDto(listOf(actorId))

            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns null

            // when
            mediaContentsService.createMediaContentsActor(
                mediaContentsId,
                mediaContentsActorRequestDto
            )
        }
        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failCreateMediaContentsActor_noActor() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val mediaContentsActorRequestDto = MediaContentsActorRequestDto(listOf(actorId))

            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns this.mediaContents
            every {
                actorRepository.findById(actorId)
            } returns null

            // when
            mediaContentsService.createMediaContentsActor(
                mediaContentsId,
                mediaContentsActorRequestDto
            )
        }
        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun successCreateMediaContentsCreator() {
        // given
        val mediaContentsCreatorRequestDto = MediaContentsCreatorRequestDto(listOf(creatorId))

        every {
            mediaContentsRepository.findById(mediaContentsId)
        } returns this.mediaContents

        every {
            creatorRepository.findById(creatorId)
        } returns this.creator

        every {
            mediaContentsCreatorRepository.save(any())
        } returns Unit

        // when
        val mediaContentsResponseDto = mediaContentsService.createMediaContentsCreator(
            mediaContentsId,
            mediaContentsCreatorRequestDto
        )

        // then
        assertEquals(this.mediaContentsId, mediaContentsResponseDto.id)
    }

    @Test
    fun failCreateMediaContentsCreator_noMediaContents() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val mediaContentsCreatorRequestDto = MediaContentsCreatorRequestDto(listOf(creatorId))

            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns null

            // when
            mediaContentsService.createMediaContentsCreator(
                mediaContentsId,
                mediaContentsCreatorRequestDto
            )
        }
        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failCreateMediaContentsCreator_noCreator() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val mediaContentsCreatorRequestDto = MediaContentsCreatorRequestDto(listOf(creatorId))

            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns this.mediaContents
            every {
                creatorRepository.findById(creatorId)
            } returns null

            // when
            mediaContentsService.createMediaContentsCreator(
                mediaContentsId,
                mediaContentsCreatorRequestDto
            )
        }
        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun successCreateMediaContentsGenre() {
        // given
        val mediaContentsGenreRequestDto = MediaContentsGenreRequestDto(listOf(genreId))

        every {
            mediaContentsRepository.findById(mediaContentsId)
        } returns this.mediaContents

        every {
            genreRepository.findById(genreId)
        } returns this.genre

        every {
            mediaContentsGenreRepository.save(any())
        } returns Unit

        // when
        val mediaContentsResponseDto = mediaContentsService.createMediaContentsGenre(
            mediaContentsId,
            mediaContentsGenreRequestDto
        )

        // then
        assertEquals(this.mediaContentsId, mediaContentsResponseDto.id)
    }

    @Test
    fun failCreateMediaContentsGenre_noMediaContents() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val mediaContentsGenreRequestDto = MediaContentsGenreRequestDto(listOf(genreId))

            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns null

            // when
            mediaContentsService.createMediaContentsGenre(
                mediaContentsId,
                mediaContentsGenreRequestDto
            )
        }
        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failCreateMediaContentsGenre_noGenre() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val mediaContentsGenreRequestDto = MediaContentsGenreRequestDto(listOf(genreId))

            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns this.mediaContents
            every {
                genreRepository.findById(genreId)
            } returns null

            // when
            mediaContentsService.createMediaContentsGenre(
                mediaContentsId,
                mediaContentsGenreRequestDto
            )
        }
        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun successDeleteMediaContentsActor() {
        // given
        val mediaContentsActorRequestDto = MediaContentsActorRequestDto(listOf(actorId))

        every {
            mediaContentsRepository.findById(mediaContentsId)
        } returns this.mediaContents

        every {
            actorRepository.findById(actorId)
        } returns this.actor

        every {
            mediaContentsActorRepository.delete(any())
        } returns Unit

        // when
        val mediaContentsResponseDto = mediaContentsService.deleteMediaContentsActor(
            mediaContentsId,
            mediaContentsActorRequestDto
        )

        // then
        assertEquals(this.mediaContentsId, mediaContentsResponseDto.id)
    }

    @Test
    fun failDeleteMediaContentsActor_noMediaContents() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val mediaContentsActorRequestDto = MediaContentsActorRequestDto(listOf(actorId))

            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns null

            // when
            mediaContentsService.deleteMediaContentsActor(
                mediaContentsId,
                mediaContentsActorRequestDto
            )
        }
        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failDeleteMediaContentsActor_noActor() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val mediaContentsActorRequestDto = MediaContentsActorRequestDto(listOf(actorId))

            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns this.mediaContents
            every {
                actorRepository.findById(actorId)
            } returns null

            // when
            mediaContentsService.deleteMediaContentsActor(
                mediaContentsId,
                mediaContentsActorRequestDto
            )
        }
        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun successDeleteMediaContentsCreator() {
        // given
        val mediaContentsCreatorRequestDto = MediaContentsCreatorRequestDto(listOf(creatorId))

        every {
            mediaContentsRepository.findById(mediaContentsId)
        } returns this.mediaContents

        every {
            creatorRepository.findById(creatorId)
        } returns this.creator

        every {
            mediaContentsCreatorRepository.delete(any())
        } returns Unit

        // when
        val mediaContentsResponseDto = mediaContentsService.deleteMediaContentsCreator(
            mediaContentsId,
            mediaContentsCreatorRequestDto
        )

        // then
        assertEquals(this.mediaContentsId, mediaContentsResponseDto.id)
    }

    @Test
    fun failDeleteMediaContentsCreator_noMediaContents() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val mediaContentsCreatorRequestDto = MediaContentsCreatorRequestDto(listOf(creatorId))

            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns null

            // when
            mediaContentsService.deleteMediaContentsCreator(
                mediaContentsId,
                mediaContentsCreatorRequestDto
            )
        }
        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failDeleteMediaContentsCreator_noCreator() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val mediaContentsCreatorRequestDto = MediaContentsCreatorRequestDto(listOf(creatorId))

            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns this.mediaContents
            every {
                creatorRepository.findById(creatorId)
            } returns null

            // when
            mediaContentsService.deleteMediaContentsCreator(
                mediaContentsId,
                mediaContentsCreatorRequestDto
            )
        }
        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun successDeleteMediaContentsGenre() {
        // given
        val mediaContentsGenreRequestDto = MediaContentsGenreRequestDto(listOf(genreId))

        every {
            mediaContentsRepository.findById(mediaContentsId)
        } returns this.mediaContents

        every {
            genreRepository.findById(genreId)
        } returns this.genre

        every {
            mediaContentsGenreRepository.delete(any())
        } returns Unit

        // when
        val mediaContentsResponseDto = mediaContentsService.deleteMediaContentsGenre(
            mediaContentsId,
            mediaContentsGenreRequestDto
        )

        // then
        assertEquals(this.mediaContentsId, mediaContentsResponseDto.id)
    }

    @Test
    fun failDeleteMediaContentsGenre_noMediaContents() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val mediaContentsGenreRequestDto = MediaContentsGenreRequestDto(listOf(genreId))

            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns null

            // when
            mediaContentsService.deleteMediaContentsGenre(
                mediaContentsId,
                mediaContentsGenreRequestDto
            )
        }
        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failDeleteMediaContentsGenre_noGenre() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val mediaContentsGenreRequestDto = MediaContentsGenreRequestDto(listOf(genreId))

            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns this.mediaContents
            every {
                genreRepository.findById(genreId)
            } returns null

            // when
            mediaContentsService.deleteMediaContentsGenre(
                mediaContentsId,
                mediaContentsGenreRequestDto
            )
        }
        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }
}
