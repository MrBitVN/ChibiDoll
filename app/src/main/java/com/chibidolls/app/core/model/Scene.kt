package com.chibidolls.app.core.model

import com.google.gson.annotations.SerializedName
import java.util.UUID

enum class SceneItemType {
    @SerializedName("DOLL") DOLL,
    @SerializedName("FURNITURE") FURNITURE,
    @SerializedName("PET") PET,
    @SerializedName("EFFECT") EFFECT,
    @SerializedName("SPEECH_BUBBLE") SPEECH_BUBBLE
}

data class SceneItem(
    @SerializedName("instanceId") val instanceId: String = UUID.randomUUID().toString(),
    @SerializedName("itemType") val itemType: SceneItemType,
    @SerializedName("referenceId") val referenceId: String,
    @SerializedName("positionX") val positionX: Float = 512f,
    @SerializedName("positionY") val positionY: Float = 768f,
    @SerializedName("scale") val scale: Float = 1.0f,
    @SerializedName("rotationDegrees") val rotationDegrees: Float = 0.0f,
    @SerializedName("isFlippedHorizontal") val isFlippedHorizontal: Boolean = false,
    @SerializedName("zOrder") val zOrder: Int = 1,
    @SerializedName("extraPayload") val extraPayload: String? = null
)

data class Scene(
    @SerializedName("id") val id: String = UUID.randomUUID().toString(),
    @SerializedName("title") val title: String = "Cute Scene",
    @SerializedName("backgroundId") val backgroundId: String = "bg_pastel_bedroom_01",
    @SerializedName("createdAt") val createdAt: Long = System.currentTimeMillis(),
    @SerializedName("updatedAt") val updatedAt: Long = System.currentTimeMillis(),
    @SerializedName("previewImagePath") val previewImagePath: String? = null,
    @SerializedName("items") val items: List<SceneItem> = emptyList()
)
