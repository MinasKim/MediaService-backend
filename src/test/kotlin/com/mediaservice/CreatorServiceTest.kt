package com.mediaservice

import com.mediaservice.application.CreatorService
import com.mediaservice.application.dto.media.CreatorCreateRequestDto
import com.mediaservice.application.dto.media.CreatorUpdateRequestDto
import com.mediaservice.domain.Creator
import com.mediaservice.domain.Role
import com.mediaservice.domain.User
import com.mediaservice.domain.repository.CreatorRepository
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

class CreatorServiceTest {
    private val creatorRepository = mockk<CreatorRepository>()
    private val creatorService: CreatorService = CreatorService(this.creatorRepository)
    private lateinit var creatorId: UUID
    private lateinit var adminId: UUID
    private lateinit var userId: UUID
    private lateinit var creator: Creator
    private lateinit var updatedCreator: Creator
    private lateinit var deletedCreator: Creator
    private lateinit var admin: User
    private lateinit var user: User

    @BeforeEach
    fun setup() {
        clearAllMocks()
        this.creatorId = UUID.randomUUID()
        this.adminId = UUID.randomUUID()
        this.userId = UUID.randomUUID()
        this.creator = Creator(this.creatorId, "test Creator", isDeleted = false)
        this.updatedCreator = Creator(this.creatorId, "update Creator", isDeleted = false)
        this.deletedCreator = Creator(this.creatorId, "test Creator", isDeleted = true)
        this.admin = User(this.adminId, "admin@gmail.com", "1234", Role.ADMIN)
        this.user = User(this.userId, "user@gmail.com", "1234", Role.USER)
    }

    @Test
    fun successCreate() {
        // given
        mockkObject(Creator)
        val creatorCreateRequestDto = CreatorCreateRequestDto("test Creator")

        every { Creator.of(creatorCreateRequestDto.name) } returns this.creator
        every { creatorRepository.save(creator) } returns this.creator

        // when
        val creatorResponseDto = this.creatorService.create(creatorCreateRequestDto)

        // then
        assertEquals(this.creator.name, creatorResponseDto.name)
    }

    @Test
    fun successUpdate() {
        // given
        val creatorUpdateRequestDto = CreatorUpdateRequestDto("update creator")

        every { creatorRepository.findById(creatorId) } returns this.creator
        every { creatorRepository.update(creatorId, creator) } returns this.updatedCreator

        // when
        val creatorResponseDto = this.creatorService.update(creatorId, creatorUpdateRequestDto)

        // then
        assertEquals(this.updatedCreator.name, creatorResponseDto.name)
    }

    @Test
    fun failUpdate_CannotFindCreator() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val creatorUpdateRequestDto = CreatorUpdateRequestDto("update Creator")

            every { creatorRepository.findById(creatorId) } returns null

            // when
            this.creatorService.update(creatorId, creatorUpdateRequestDto)
        }

        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failUpdate_DeletedCreator() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val creatorUpdateRequestDto = CreatorUpdateRequestDto("update Creator")

            every { creatorRepository.findById(creatorId) } returns this.deletedCreator

            // when
            this.creatorService.update(creatorId, creatorUpdateRequestDto)
        }

        // then
        assertEquals(ErrorCode.ROW_ALREADY_DELETED, exception.errorCode)
    }

    @Test
    fun successDelete() {
        // given
        every { creatorRepository.findById(creatorId) } returns this.creator
        every { creatorRepository.delete(creatorId) } returns this.deletedCreator

        // when
        val creatorResponseDto = this.creatorService.delete(creatorId)

        // then
        assertEquals(this.deletedCreator.isDeleted, true)
    }

    @Test
    fun failDelete_CannotFindCreator() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            every { creatorRepository.findById(creatorId) } returns null

            // when
            this.creatorService.delete(creatorId)
        }

        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failDelete_DeletedCreator() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            every { creatorRepository.findById(creatorId) } returns this.deletedCreator

            // when
            this.creatorService.delete(creatorId)
        }

        // then
        assertEquals(ErrorCode.ROW_ALREADY_DELETED, exception.errorCode)
    }

    @Test
    fun findCreator() {
        // given
        every { creatorRepository.findAll() } returns listOf(this.creator)

        // when
        val creators = this.creatorService.findAll()

        // then
        assertEquals(1, creators!!.size)
    }

    @Test
    fun failFindCreator() {
        // given
        val exception = assertThrows(BadRequestException::class.java) {

            every { creatorRepository.findAll() } returns null

            // when
            val creators = this.creatorService.findAll()
        }

        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }
}
