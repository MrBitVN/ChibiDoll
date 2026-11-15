package com.chibidolls.app.core.repository

import android.content.Context
import android.content.SharedPreferences
import com.chibidolls.app.core.performance.DevicePerformanceHelper
import com.chibidolls.app.core.performance.ParticleDensity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("chibi_settings", Context.MODE_PRIVATE)

    private val isLowSpec = DevicePerformanceHelper.isLowEndDevice(context)

    private val _sceneEffectsEnabled = MutableStateFlow(
        prefs.getBoolean(KEY_SCENE_EFFECTS, true)
    )
    val sceneEffectsEnabled: StateFlow<Boolean> = _sceneEffectsEnabled.asStateFlow()

    private val _idleAnimationsEnabled = MutableStateFlow(
        prefs.getBoolean(KEY_IDLE_ANIMATIONS, true)
    )
    val idleAnimationsEnabled: StateFlow<Boolean> = _idleAnimationsEnabled.asStateFlow()

    private val defaultDensity = if (isLowSpec) ParticleDensity.LOW.name else ParticleDensity.HIGH.name
    private val _particleDensity = MutableStateFlow(
        runCatching {
            ParticleDensity.valueOf(prefs.getString(KEY_PARTICLE_DENSITY, defaultDensity) ?: defaultDensity)
        }.getOrDefault(if (isLowSpec) ParticleDensity.LOW else ParticleDensity.HIGH)
    )
    val particleDensity: StateFlow<ParticleDensity> = _particleDensity.asStateFlow()

    private val _bgmEnabled = MutableStateFlow(prefs.getBoolean(KEY_BGM, true))
    val bgmEnabled: StateFlow<Boolean> = _bgmEnabled.asStateFlow()

    private val _sfxEnabled = MutableStateFlow(prefs.getBoolean(KEY_SFX, true))
    val sfxEnabled: StateFlow<Boolean> = _sfxEnabled.asStateFlow()

    private val _unlockedItemIds = MutableStateFlow<Set<String>>(
        prefs.getStringSet(KEY_UNLOCKED_ITEMS, emptySet()) ?: emptySet()
    )
    val unlockedItemIds: StateFlow<Set<String>> = _unlockedItemIds.asStateFlow()

    private val _unlockedPackIds = MutableStateFlow<Set<String>>(
        prefs.getStringSet(KEY_UNLOCKED_PACKS, emptySet()) ?: emptySet()
    )
    val unlockedPackIds: StateFlow<Set<String>> = _unlockedPackIds.asStateFlow()

    private val _unlockedDollSlots = MutableStateFlow<Set<Int>>(
        prefs.getStringSet(KEY_UNLOCKED_DOLL_SLOTS, emptySet())?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    )
    val unlockedDollSlots: StateFlow<Set<Int>> = _unlockedDollSlots.asStateFlow()

    private val _unlockedAlbumSlots = MutableStateFlow<Set<Int>>(
        prefs.getStringSet(KEY_UNLOCKED_ALBUM_SLOTS, emptySet())?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    )
    val unlockedAlbumSlots: StateFlow<Set<Int>> = _unlockedAlbumSlots.asStateFlow()

    val isDeviceLowSpec: Boolean get() = isLowSpec

    fun isPackUnlocked(packId: String): Boolean {
        // All packs are unlocked per user requirements
        return true
    }

    fun unlockPackId(packId: String) {
        val updated = _unlockedPackIds.value.toMutableSet()
        updated.add(packId)
        _unlockedPackIds.value = updated
        prefs.edit().putStringSet(KEY_UNLOCKED_PACKS, updated).apply()
    }

    fun isDollSlotUnlocked(slotIndex: Int): Boolean {
        if (slotIndex < 4) return true
        return _unlockedDollSlots.value.contains(slotIndex)
    }

    fun unlockDollSlot(slotIndex: Int) {
        val updated = _unlockedDollSlots.value.toMutableSet()
        updated.add(slotIndex)
        _unlockedDollSlots.value = updated
        prefs.edit().putStringSet(KEY_UNLOCKED_DOLL_SLOTS, updated.map { it.toString() }.toSet()).apply()
    }

    fun isAlbumSlotUnlocked(slotIndex: Int): Boolean {
        if (slotIndex < 3) return true
        return _unlockedAlbumSlots.value.contains(slotIndex)
    }

    fun unlockAlbumSlot(slotIndex: Int) {
        val updated = _unlockedAlbumSlots.value.toMutableSet()
        updated.add(slotIndex)
        _unlockedAlbumSlots.value = updated
        prefs.edit().putStringSet(KEY_UNLOCKED_ALBUM_SLOTS, updated.map { it.toString() }.toSet()).apply()
    }

    fun unlockAllContent() {
        val allPacks = setOf("halloween", "princess", "winter", "kpop", "jewelry")
        val updatedPacks = _unlockedPackIds.value.toMutableSet()
        updatedPacks.addAll(allPacks)
        _unlockedPackIds.value = updatedPacks
        prefs.edit().putStringSet(KEY_UNLOCKED_PACKS, updatedPacks).apply()

        val allSlots = setOf(4, 5)
        _unlockedDollSlots.value = allSlots
        prefs.edit().putStringSet(KEY_UNLOCKED_DOLL_SLOTS, allSlots.map { it.toString() }.toSet()).apply()

        val allAlbumSlots = setOf(3, 4, 5)
        _unlockedAlbumSlots.value = allAlbumSlots
        prefs.edit().putStringSet(KEY_UNLOCKED_ALBUM_SLOTS, allAlbumSlots.map { it.toString() }.toSet()).apply()
    }

    fun isItemUnlocked(itemId: String, isPremium: Boolean): Boolean {
        if (!isPremium) return true
        return _unlockedItemIds.value.contains(itemId)
    }

    fun unlockItem(itemId: String) {
        val updated = _unlockedItemIds.value.toMutableSet()
        updated.add(itemId)
        _unlockedItemIds.value = updated
        prefs.edit().putStringSet(KEY_UNLOCKED_ITEMS, updated).apply()
    }

    fun unlockPack(itemIds: List<String>) {
        val updated = _unlockedItemIds.value.toMutableSet()
        updated.addAll(itemIds)
        _unlockedItemIds.value = updated
        prefs.edit().putStringSet(KEY_UNLOCKED_ITEMS, updated).apply()
    }

    fun setSceneEffectsEnabled(enabled: Boolean) {
        _sceneEffectsEnabled.value = enabled
        prefs.edit().putBoolean(KEY_SCENE_EFFECTS, enabled).apply()
    }

    fun setIdleAnimationsEnabled(enabled: Boolean) {
        _idleAnimationsEnabled.value = enabled
        prefs.edit().putBoolean(KEY_IDLE_ANIMATIONS, enabled).apply()
    }

    fun setParticleDensity(density: ParticleDensity) {
        _particleDensity.value = density
        prefs.edit().putString(KEY_PARTICLE_DENSITY, density.name).apply()
    }

    fun setBgmEnabled(enabled: Boolean) {
        _bgmEnabled.value = enabled
        prefs.edit().putBoolean(KEY_BGM, enabled).apply()
    }

    fun setSfxEnabled(enabled: Boolean) {
        _sfxEnabled.value = enabled
        prefs.edit().putBoolean(KEY_SFX, enabled).apply()
    }

    companion object {
        private const val KEY_SCENE_EFFECTS = "key_scene_effects"
        private const val KEY_IDLE_ANIMATIONS = "key_idle_animations"
        private const val KEY_PARTICLE_DENSITY = "key_particle_density"
        private const val KEY_BGM = "key_bgm"
        private const val KEY_SFX = "key_sfx"
        private const val KEY_UNLOCKED_ITEMS = "key_unlocked_items"
        private const val KEY_UNLOCKED_PACKS = "key_unlocked_packs"
        private const val KEY_UNLOCKED_DOLL_SLOTS = "key_unlocked_doll_slots"
        private const val KEY_UNLOCKED_ALBUM_SLOTS = "key_unlocked_album_slots"
    }
}
