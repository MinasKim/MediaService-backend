package com.mediaservice.domain.repository

import com.mediaservice.domain.Genre
import com.mediaservice.domain.GenreEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class GenreRepository {
    fun save(genre: Genre): Genre {
        return GenreEntity.new {
            name = genre.name
            isDeleted = genre.isDeleted
        }.let { return Genre.from(it) }
    }

    fun findById(id: UUID): Genre? {
        return GenreEntity.findById(id)?.let {
            Genre.from(it)
        }
    }

    fun findAll(): List<Genre> {
        return GenreEntity.all().map {
            Genre.from(it)
        }.toList()
    }

    fun update(id: UUID, genre: Genre): Genre? {
        return GenreEntity.findById(id)?.let {
            it.name = genre.name
            return Genre.from(it)
        }
    }

    fun delete(id: UUID): Genre? {
        return GenreEntity.findById(id)?.let {
            it.isDeleted = true
            return Genre.from(it)
        }
    }
}
