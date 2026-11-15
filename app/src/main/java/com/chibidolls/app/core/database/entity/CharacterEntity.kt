package com.chibidolls.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chibidolls.app.core.model.Character
import com.chibidolls.app.core.model.OutfitSlot

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: String,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long,
    val previewImagePath: String?,
    val tags: List<String> = emptyList(),
    val bio: String? = null,
    val slots: Map<String, OutfitSlot>
) {
    fun toDomain(): Character {
        return Character(
            id = id,
            name = name,
            createdAt = createdAt,
            updatedAt = updatedAt,
            previewImagePath = previewImagePath,
            tags = tags,
            bio = bio,
            slots = slots
        )
    }

    companion object {
        fun fromDomain(domain: Character): CharacterEntity {
            return CharacterEntity(
                id = domain.id,
                name = domain.name,
                createdAt = domain.createdAt,
                updatedAt = domain.updatedAt,
                previewImagePath = domain.previewImagePath,
                tags = domain.tags,
                bio = domain.bio,
                slots = domain.slots
            )
        }
    }
}
