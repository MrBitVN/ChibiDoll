package com.chibidolls.app.core.model

import com.google.gson.annotations.SerializedName

enum class CreationType {
    @SerializedName("CHARACTER") CHARACTER,
    @SerializedName("SCENE") SCENE
}

data class SavedCreation(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: CreationType,
    @SerializedName("title") val title: String,
    @SerializedName("thumbnailPath") val thumbnailPath: String?,
    @SerializedName("createdAt") val createdAt: Long,
    @SerializedName("updatedAt") val updatedAt: Long,
    @SerializedName("tags") val tags: List<String> = emptyList(),
    @SerializedName("bio") val bio: String? = null
)
