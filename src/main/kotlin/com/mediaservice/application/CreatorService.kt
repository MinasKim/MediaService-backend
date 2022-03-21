package com.mediaservice.application

import com.mediaservice.application.dto.media.CreatorCreateRequestDto
import com.mediaservice.application.dto.media.CreatorResponseDto
import com.mediaservice.application.dto.media.CreatorUpdateRequestDto
import com.mediaservice.application.validator.IsDeletedValidator
import com.mediaservice.application.validator.Validator
import com.mediaservice.domain.Creator
import com.mediaservice.domain.repository.CreatorRepository
import com.mediaservice.exception.BadRequestException
import com.mediaservice.exception.ErrorCode
import com.mediaservice.exception.InternalServerException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CreatorService(
    private val creatorRepository: CreatorRepository
) {
    @Transactional
    fun create(creatorCreateRequestDto: CreatorCreateRequestDto): CreatorResponseDto {
        return CreatorResponseDto.from(
            this.creatorRepository.save(
                Creator.of(creatorCreateRequestDto.name)
            )
        )
    }
    @Transactional
    fun update(id: UUID, creatorCreateRequestDto: CreatorUpdateRequestDto): CreatorResponseDto {
        val creatorForUpdate = this.creatorRepository.findById(id) ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST, "NO SUCH CREATOR $id"
        )

        val validator: Validator = IsDeletedValidator(creatorForUpdate.isDeleted, Creator.DOMAIN)
        validator.validate()

        creatorForUpdate.update(creatorCreateRequestDto.name)

        return CreatorResponseDto.from(
            this.creatorRepository.update(
                id,
                creatorForUpdate
            ) ?: throw InternalServerException(
                ErrorCode.INTERNAL_SERVER, "CREATOR IS CHECKED, BUT EXCEPTION OCCURS"
            )
        )
    }

    @Transactional
    fun delete(id: UUID): CreatorResponseDto {
        val creatorForUpdate = this.creatorRepository.findById(id) ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST, "NO SUCH CREATOR $id"
        )

        val validator: Validator = IsDeletedValidator(creatorForUpdate.isDeleted, Creator.DOMAIN)
        validator.validate()

        return CreatorResponseDto.from(
            this.creatorRepository.delete(
                id
            ) ?: throw BadRequestException(
                ErrorCode.ROW_DOES_NOT_EXIST, "NO SUCH CREATOR $id"
            )
        )
    }

    @Transactional
    fun findAll(): List<CreatorResponseDto>? {
        return this.creatorRepository.findAll()?.map {
            CreatorResponseDto.from(it)
        }?.toList() ?: throw BadRequestException(
            ErrorCode.ROW_DOES_NOT_EXIST, "THERE IS NO CREATOR"
        )
    }
}
