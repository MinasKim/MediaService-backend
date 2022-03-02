package com.mediaservice

import com.mediaservice.application.MediaContentsService
import com.mediaservice.domain.Actor
import com.mediaservice.domain.Creator
import com.mediaservice.domain.Genre
import com.mediaservice.domain.Media
import com.mediaservice.domain.MediaContents
import com.mediaservice.domain.MediaSeries
import com.mediaservice.domain.Profile
import com.mediaservice.domain.Role
import com.mediaservice.domain.User
import com.mediaservice.domain.repository.LikeRepository
import com.mediaservice.domain.repository.MediaContentsRepository
import com.mediaservice.domain.repository.MediaRepository
import com.mediaservice.domain.repository.MediaSeriesRepository
import com.mediaservice.domain.repository.ProfileRepository
import com.mediaservice.exception.BadRequestException
import com.mediaservice.exception.ErrorCode
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
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
    private var likeRepository = mockk<LikeRepository>()
    private val mediaContentsService: MediaContentsService =
        MediaContentsService(
            this.mediaRepository, this.mediaSeriesRepository, this.mediaContentsRepository,
            this.profileRepository, this.likeRepository
        )
    private lateinit var media: Media
    private lateinit var mediaSeries: MediaSeries
    private lateinit var mediaContents: MediaContents
    private lateinit var mediaContentsNoActor: MediaContents
    private lateinit var mediaContentsNoGenre: MediaContents
    private lateinit var mediaContentsNoCreator: MediaContents
    private lateinit var user: User
    private lateinit var profile: Profile
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

    @BeforeEach
    fun setup() {
        clearAllMocks()
        this.mediaId = UUID.randomUUID()
        this.mediaSeriesId = UUID.randomUUID()
        this.mediaContentsId = UUID.randomUUID()
        this.userId = UUID.randomUUID()
        this.profileId = UUID.randomUUID()
        this.actorList = listOf(Actor(UUID.randomUUID(), "testActor", false))
        this.genreList = listOf(Genre(UUID.randomUUID(), "testGenre", false))
        this.creatorList = listOf(Creator(UUID.randomUUID(), "testCreator", false))
        this.user = User(userId, "test@gmail.com", "test123!@", Role.USER)
        this.profile = Profile(profileId, this.user, "test profile", "19+", "test.jpg", false)
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
    fun successFindMediaContentsById() {
        // given
        every {
            profileRepository.findById(profileId)
        } returns this.profile
        every {
            mediaContentsRepository.findById(mediaContentsId)
        } returns this.mediaContents
        every {
            mediaSeriesRepository.findByMediaAllSeriesId(any())
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
    fun failFindDetail_NoMediaSeriesList() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            every {
                profileRepository.findById(profileId)
            } returns this.profile
            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns this.mediaContents
            every {
                mediaSeriesRepository.findByMediaAllSeriesId(any())
            } returns null

            // when
            mediaContentsService.findMediaContentsById(this.userId, this.profileId, this.mediaContentsId)
        }
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failFindDetail_NoMediaList() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            every {
                profileRepository.findById(profileId)
            } returns this.profile
            every {
                mediaContentsRepository.findById(mediaContentsId)
            } returns this.mediaContents
            every {
                mediaSeriesRepository.findByMediaAllSeriesId(any())
            } returns listOf(this.mediaSeries)
            every {
                mediaRepository.findByMediaSeriesId(any())
            } returns null

            // when
            mediaContentsService.findMediaContentsById(this.userId, this.profileId, this.mediaContentsId)
        }
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }
}