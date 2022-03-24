package com.mediaservice

import com.mediaservice.application.GenreService
import com.mediaservice.application.dto.media.GenreCreateRequestDto
import com.mediaservice.application.dto.media.GenreUpdateRequestDto
import com.mediaservice.domain.Genre
import com.mediaservice.domain.Profile
import com.mediaservice.domain.Role
import com.mediaservice.domain.User
import com.mediaservice.domain.repository.GenreRepository
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

class GenreServiceTest {
    private val genreRepository = mockk<GenreRepository>()
    private val profileRepository = mockk<ProfileRepository>()
    private val genreService: GenreService = GenreService(this.genreRepository, this.profileRepository)
    private lateinit var genreId: UUID
    private lateinit var adminId: UUID
    private lateinit var userId: UUID
    private lateinit var profileId: UUID
    private lateinit var profile: Profile
    private lateinit var genre: Genre
    private lateinit var updatedGenre: Genre
    private lateinit var deletedGenre: Genre
    private lateinit var admin: User
    private lateinit var user: User

    @BeforeEach
    fun setup() {
        clearAllMocks()
        this.genreId = UUID.randomUUID()
        this.adminId = UUID.randomUUID()
        this.userId = UUID.randomUUID()
        this.profileId = UUID.randomUUID()
        this.genre = Genre(this.genreId, "test Genre", isDeleted = false)
        this.updatedGenre = Genre(this.genreId, "update Genre", isDeleted = false)
        this.deletedGenre = Genre(this.genreId, "test Genre", isDeleted = true)
        this.admin = User(this.adminId, "admin@gmail.com", "1234", Role.ADMIN)
        this.user = User(this.userId, "user@gmail.com", "1234", Role.USER)
        this.profile = Profile(profileId, this.user, "test profile", "19+", "test.jpg", false)
    }

    @Test
    fun successCreate() {
        // given
        mockkObject(Genre)
        val genreCreateRequestDto = GenreCreateRequestDto("test Genre")

        every { Genre.of(genreCreateRequestDto.name) } returns this.genre
        every { genreRepository.save(genre) } returns this.genre

        // when
        val genreResponseDto = this.genreService.create(genreCreateRequestDto)

        // then
        assertEquals(this.genre.name, genreResponseDto.name)
    }

    @Test
    fun successUpdate() {
        // given
        val genreUpdateRequestDto = GenreUpdateRequestDto("update Genre")

        every { genreRepository.findById(genreId) } returns this.genre
        every { genreRepository.update(genreId, genre) } returns this.updatedGenre

        // when
        val actorResponseDto = this.genreService.update(genreId, genreUpdateRequestDto)

        // then
        assertEquals(this.updatedGenre.name, actorResponseDto.name)
    }

    @Test
    fun failUpdate_CannotFindGenre() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val genreUpdateRequestDto = GenreUpdateRequestDto("update Genre")

            every { genreRepository.findById(genreId) } returns null

            // when
            this.genreService.update(genreId, genreUpdateRequestDto)
        }

        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failUpdate_DeletedGenre() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val genreUpdateRequestDto = GenreUpdateRequestDto("update Genre")

            every { genreRepository.findById(genreId) } returns this.deletedGenre

            // when
            this.genreService.update(genreId, genreUpdateRequestDto)
        }

        // then
        assertEquals(ErrorCode.ROW_ALREADY_DELETED, exception.errorCode)
    }

    @Test
    fun successDelete() {
        // given
        every { genreRepository.findById(genreId) } returns this.genre
        every { genreRepository.delete(genreId) } returns this.deletedGenre

        // when
        val actorResponseDto = this.genreService.delete(genreId)

        // then
        assertEquals(this.deletedGenre.isDeleted, true)
    }

    @Test
    fun failDelete_CannotFindGenre() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            every { genreRepository.findById(genreId) } returns null

            // when
            this.genreService.delete(genreId)
        }

        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failDelete_DeletedGenre() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            every { genreRepository.findById(genreId) } returns this.deletedGenre

            // when
            this.genreService.delete(genreId)
        }

        // then
        assertEquals(ErrorCode.ROW_ALREADY_DELETED, exception.errorCode)
    }

    @Test
    fun findGenre() {
        // given
        every { genreRepository.findAll() } returns listOf(this.genre)
        every { profileRepository.findById(profileId) } returns this.profile

        // when
        val genres = this.genreService.findAll(this.userId, this.profileId)

        // then
        assertEquals(1, genres.size)
    }
}
