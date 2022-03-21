package com.mediaservice

import com.mediaservice.application.WishContentService
import com.mediaservice.application.dto.media.WishContentRequestDto
import com.mediaservice.application.dto.media.WishContentResponseDto
import com.mediaservice.domain.Actor
import com.mediaservice.domain.Creator
import com.mediaservice.domain.Genre
import com.mediaservice.domain.MediaContents
import com.mediaservice.domain.Profile
import com.mediaservice.domain.Role
import com.mediaservice.domain.User
import com.mediaservice.domain.WishContent
import com.mediaservice.domain.repository.MediaContentsRepository
import com.mediaservice.domain.repository.ProfileRepository
import com.mediaservice.domain.repository.WishContentRepository
import com.mediaservice.exception.BadRequestException
import com.mediaservice.exception.ErrorCode
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class WishContentServiceTest {
    private val wishContentRepository = mockk<WishContentRepository>()
    private val profileRepository = mockk<ProfileRepository>()
    private val mediaContentsRepository = mockk<MediaContentsRepository>()
    private val wishContentService: WishContentService = WishContentService(this.wishContentRepository, this.profileRepository, this.mediaContentsRepository)
    private lateinit var profileId: UUID
    private lateinit var mediaContentsId: UUID
    private lateinit var userId: UUID
    private lateinit var wishContentId: UUID
    private lateinit var profile: Profile
    private lateinit var user: User
    private lateinit var wishContent: WishContent
    private lateinit var mediaContents: MediaContents

    private lateinit var wishContentRequestDto: WishContentRequestDto
    private lateinit var wishContentResponseDto: WishContentResponseDto
    private lateinit var actorList: List<Actor>
    private lateinit var genreList: List<Genre>
    private lateinit var creatorList: List<Creator>

    @BeforeEach
    fun setup() {
        clearAllMocks()
        this.profileId = UUID.randomUUID()
        this.mediaContentsId = UUID.randomUUID()
        this.userId = UUID.randomUUID()
        this.wishContentId = UUID.randomUUID()

        this.actorList = listOf(Actor(UUID.randomUUID(), "testActor", false))
        this.genreList = listOf(Genre(UUID.randomUUID(), "testGenre", false))
        this.creatorList = listOf(Creator(UUID.randomUUID(), "testCreator", false))

        this.user = User(userId, "user@gmail.com", "1234", Role.USER)
        this.profile = Profile(profileId, user, "action", "19+", "image_url", false)
        this.mediaContents = MediaContents(
            mediaContentsId, "title", "synopsis", "trailer",
            "test thumbnail url", "19+", false, false, this.actorList, this.genreList, this.creatorList
        )
        this.wishContent = WishContent(wishContentId, mediaContents, profile, false)

        this.wishContentRequestDto = WishContentRequestDto(mediaContentsId)
        this.wishContentResponseDto = WishContentResponseDto(wishContentId, "action", "title", "synopsis", "terailer", "thumbnail", "rate", false)
    }

    @Test
    fun successFind() {
        // given
        every { wishContentRepository.findByProfileId(profileId) } returns listOf(wishContent)

        // when
        val wishContentResponseDtoList = this.wishContentService.findByProfileId(profileId)
    }
    @Test
    fun successDelete() {
        // given
        every { mediaContentsRepository.findById(mediaContentsId) } returns mediaContents
        every { profileRepository.findById(profileId) } returns profile
        every { wishContentRepository.findByProfileIdAndMediaAllSeriesId(profileId, mediaContentsId) } returns false
        every { wishContentRepository.delete(any()) } returns listOf(wishContent)

        // when
        val wishContentResponseDtoList = this.wishContentService.deleteWishContent(wishContentRequestDto, profileId)

        // then
        assertEquals(wishContentResponseDtoList.get(0).profileName, wishContent.profile.name)
    }
    @Test
    fun successCreate() {
        // given
        every { mediaContentsRepository.findById(mediaContentsId) } returns mediaContents
        every { profileRepository.findById(profileId) } returns profile
        every { wishContentRepository.existsByProfileIdAndMediaAllSeriesId(profileId, mediaContentsId) } returns false
        every { wishContentRepository.save(any()) } returns wishContent

        // when
        val wishContentResponseDto = this.wishContentService.createWishContent(wishContentRequestDto, profileId)

        // then
        assertEquals(wishContentResponseDto.profileName, wishContent.profile.name)
    }

    @Test
    fun failCreate_AlreadyInserted() {
        val exception = Assertions.assertThrows(BadRequestException::class.java) {
            // given
            every { mediaContentsRepository.findById(mediaContentsId) } returns mediaContents
            every { profileRepository.findById(profileId) } returns profile
            every { wishContentRepository.existsByProfileIdAndMediaAllSeriesId(profileId, mediaContentsId) } returns true
            every { wishContentRepository.save(any()) } returns wishContent

            // when
            this.wishContentService.createWishContent(wishContentRequestDto, profileId)
        }

        // then
        assertEquals(ErrorCode.ROW_ALREADY_EXIST, exception.errorCode)
    }

    @Test
    fun failDelete_noProfile() {
        val exception = Assertions.assertThrows(BadRequestException::class.java) {
            // given
            every { mediaContentsRepository.findById(mediaContentsId) } returns mediaContents
            every { profileRepository.findById(profileId) } returns null
            every { wishContentRepository.findByProfileIdAndMediaAllSeriesId(profileId, mediaContentsId) } returns false
            every { wishContentRepository.save(any()) } returns wishContent

            // when
            this.wishContentService.deleteWishContent(wishContentRequestDto, profileId)
        }
        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failDelete_noMediaAllSeries() {
        val exception = Assertions.assertThrows(BadRequestException::class.java) {
            // given
            every { mediaContentsRepository.findById(mediaContentsId) } returns null
            every { profileRepository.findById(profileId) } returns profile
            every { wishContentRepository.findByProfileIdAndMediaAllSeriesId(profileId, mediaContentsId) } returns false
            every { wishContentRepository.save(any()) } returns wishContent

            // when
            this.wishContentService.deleteWishContent(wishContentRequestDto, profileId)
        }
        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }
}
