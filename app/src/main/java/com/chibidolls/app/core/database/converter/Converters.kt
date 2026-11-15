package com.chibidolls.app.core.database.converter

import androidx.room.TypeConverter
import com.chibidolls.app.core.model.OutfitSlot
import com.chibidolls.app.core.model.SceneItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromSlotsMap(value: Map<String, OutfitSlot>?): String {
        return gson.toJson(value ?: emptyMap<String, OutfitSlot>())
    }

    @TypeConverter
    fun toSlotsMap(value: String?): Map<String, OutfitSlot> {
        if (value.isNullOrEmpty()) return emptyMap()
        val type = object : TypeToken<Map<String, OutfitSlot>>() {}.type
        return gson.fromJson(value, type) ?: emptyMap()
    }

    @TypeConverter
    fun fromSceneItemList(value: List<SceneItem>?): String {
        return gson.toJson(value ?: emptyList<SceneItem>())
    }

    @TypeConverter
    fun toSceneItemList(value: String?): List<SceneItem> {
        if (value.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<SceneItem>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value ?: emptyList<String>())
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }
}
