package com.chibidolls.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chibidolls.app.core.model.Scene
import com.chibidolls.app.core.model.SceneItem

@Entity(tableName = "scenes")
data class SceneEntity(
    @PrimaryKey val id: String,
    val title: String,
    val backgroundId: String,
    val createdAt: Long,
    val updatedAt: Long,
    val previewImagePath: String?,
    val items: List<SceneItem>
) {
    fun toDomain(): Scene {
        return Scene(
            id = id,
            title = title,
            backgroundId = backgroundId,
            createdAt = createdAt,
            updatedAt = updatedAt,
            previewImagePath = previewImagePath,
            items = items
        )
    }

    companion object {
        fun fromDomain(domain: Scene): SceneEntity {
            return SceneEntity(
                id = domain.id,
                title = domain.title,
                backgroundId = domain.backgroundId,
                createdAt = domain.createdAt,
                updatedAt = domain.updatedAt,
                previewImagePath = domain.previewImagePath,
                items = domain.items
            )
        }
    }
}
