package com.mediaservice.application

import com.mediaservice.application.dto.media.ActorCreateRequestDto
import com.mediaservice.application.dto.media.ActorResponseDto
import com.mediaservice.application.dto.media.ActorUpdateRequestDto
import com.mediaservice.application.validator.IdEqualValidator
import com.mediaservice.application.validator.IsDeletedValidator
import com.mediaservice.application.validator.Validator
import com.mediaservice.domain.Actor
import com.mediaservice.domain.Profile
import com.mediaservice.domain.repository.ActorRepository
import com.mediaservice.domain.repository.ProfileRepository
import com.mediaservice.exception.BadRequestException
import com.mediaservice.exception.ErrorCode
import com.mediaservice.exception.InternalServerException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ActorService(
    private val actorRepository: ActorRepository,
    private val profileRepository: ProfileRepository
) {
    @Transactional
    fun create(actorCreateRequestDto: ActorCreateRequestDto): ActorResponseDto {
        return ActorResponseDto.from(
            this.actorRepository.save(
                Actor.of(actorCreateRequestDto.name)
            )
        )
    }

    @Transactional
    fun update(id: UUID, actorUpdateRequestDto: ActorUpdateRequestDto): ActorResponseDto {
        val actorForUpdate = this.actorRepository.findById(id) ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST, "NO SUCH ACTOR $id"
        )

        val validator: Validator = IsDeletedValidator(actorForUpdate.isDeleted, Actor.DOMAIN)
        validator.validate()

        actorForUpdate.update(actorUpdateRequestDto.name)

        return ActorResponseDto.from(
            this.actorRepository.update(
                id,
                actorForUpdate
            ) ?: throw InternalServerException(
                ErrorCode.INTERNAL_SERVER, "ACTOR IS CHECKED, BUT EXCEPTION OCCURS"
            )
        )
    }

    @Transactional
    fun delete(id: UUID): ActorResponseDto {
        val actorForUpdate = this.actorRepository.findById(id) ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST, "NO SUCH ACTOR $id"
        )

        val validator: Validator = IsDeletedValidator(actorForUpdate.isDeleted, Actor.DOMAIN)
        validator.validate()

        return ActorResponseDto.from(
            this.actorRepository.delete(
                id
            ) ?: throw BadRequestException(
                ErrorCode.ROW_DOES_NOT_EXIST, "NO SUCH ACTOR $id"
            )
        )
    }

    @Transactional(readOnly = true)
    fun findAll(
        userId: UUID,
        profileId: UUID
    ): List<ActorResponseDto> {
        val profile = this.profileRepository.findById(profileId)
            ?: throw BadRequestException(
                ErrorCode.ROW_DOES_NOT_EXIST,
                "NO SUCH PROFILE $profileId"
            )

        val validator = IsDeletedValidator(profile.isDeleted, Profile.DOMAIN)
        validator.linkWith(IdEqualValidator(userId, profile.user.id!!))
        validator.validate()

        return this.actorRepository.findAll().map {
            ActorResponseDto.from(it)
        }.toList()
    }
}
