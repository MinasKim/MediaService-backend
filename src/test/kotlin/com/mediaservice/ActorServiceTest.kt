package com.mediaservice

import com.mediaservice.application.ActorService
import com.mediaservice.application.dto.media.ActorCreateRequestDto
import com.mediaservice.application.dto.media.ActorUpdateRequestDto
import com.mediaservice.domain.Actor
import com.mediaservice.domain.Profile
import com.mediaservice.domain.Role
import com.mediaservice.domain.User
import com.mediaservice.domain.repository.ActorRepository
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

class ActorServiceTest {
    private val actorRepository = mockk<ActorRepository>()
    private val profileRepository = mockk<ProfileRepository>()
    private val actorService: ActorService = ActorService(this.actorRepository, this.profileRepository)
    private lateinit var actorId: UUID
    private lateinit var adminId: UUID
    private lateinit var userId: UUID
    private lateinit var profileId: UUID
    private lateinit var profile: Profile
    private lateinit var actor: Actor
    private lateinit var updatedActor: Actor
    private lateinit var deletedActor: Actor
    private lateinit var admin: User
    private lateinit var user: User

    @BeforeEach
    fun setup() {
        clearAllMocks()
        this.actorId = UUID.randomUUID()
        this.adminId = UUID.randomUUID()
        this.userId = UUID.randomUUID()
        this.profileId = UUID.randomUUID()
        this.actor = Actor(this.actorId, "test Actor", isDeleted = false)
        this.updatedActor = Actor(this.actorId, "update Actor", isDeleted = false)
        this.deletedActor = Actor(this.actorId, "test Actor", isDeleted = true)
        this.admin = User(this.adminId, "admin@gmail.com", "1234", Role.ADMIN)
        this.user = User(this.userId, "user@gmail.com", "1234", Role.USER)
        this.profile = Profile(profileId, this.user, "test profile", "19+", "test.jpg", false)
    }

    @Test
    fun successCreate() {
        // given
        mockkObject(Actor)
        val actorCreateRequestDto = ActorCreateRequestDto("test Actor")

        every { Actor.of(actorCreateRequestDto.name) } returns this.actor
        every { actorRepository.save(actor) } returns this.actor

        // when
        val actorResponseDto = this.actorService.create(actorCreateRequestDto)

        // then
        assertEquals(this.actor.name, actorResponseDto.name)
    }

    @Test
    fun successUpdate() {
        // given
        val actorUpdateRequestDto = ActorUpdateRequestDto("update Actor")

        every { actorRepository.findById(actorId) } returns this.actor
        every { actorRepository.update(actorId, actor) } returns this.updatedActor

        // when
        val actorResponseDto = this.actorService.update(actorId, actorUpdateRequestDto)

        // then
        assertEquals(this.updatedActor.name, actorResponseDto.name)
    }

    @Test
    fun failUpdate_CannotFindActor() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val actorUpdateRequestDto = ActorUpdateRequestDto("update Actor")

            every { actorRepository.findById(actorId) } returns null

            // when
            this.actorService.update(actorId, actorUpdateRequestDto)
        }

        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failUpdate_DeletedActor() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            val actorUpdateRequestDto = ActorUpdateRequestDto("update Actor")

            every { actorRepository.findById(actorId) } returns this.deletedActor

            // when
            this.actorService.update(actorId, actorUpdateRequestDto)
        }

        // then
        assertEquals(ErrorCode.ROW_ALREADY_DELETED, exception.errorCode)
    }

    @Test
    fun successDelete() {
        // given
        every { actorRepository.findById(actorId) } returns this.actor
        every { actorRepository.delete(actorId) } returns this.deletedActor

        // when
        val actorResponseDto = this.actorService.delete(actorId)

        // then
        assertEquals(this.deletedActor.isDeleted, true)
    }

    @Test
    fun failDelete_CannotFindActor() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            every { actorRepository.findById(actorId) } returns null

            // when
            this.actorService.delete(actorId)
        }

        // then
        assertEquals(ErrorCode.ROW_DOES_NOT_EXIST, exception.errorCode)
    }

    @Test
    fun failDelete_DeletedActor() {
        val exception = assertThrows(BadRequestException::class.java) {
            // given
            every { actorRepository.findById(actorId) } returns this.deletedActor

            // when
            this.actorService.delete(actorId)
        }

        // then
        assertEquals(ErrorCode.ROW_ALREADY_DELETED, exception.errorCode)
    }

    @Test
    fun findActor() {
        // given
        every { actorRepository.findAll() } returns listOf(this.actor)
        every { profileRepository.findById(profileId) } returns this.profile

        // when
        val actors = this.actorService.findAll(this.userId, this.profileId)

        // then
        assertEquals(1, actors.size)
    }
}
