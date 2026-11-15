package com.chibidolls.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChibiDollsApp : Application(), ImageLoaderFactory {

    companion object {
        lateinit var instance: ChibiDollsApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(64 * 1024 * 1024) // 64 MB
                    .build()
            }
            .respectCacheHeaders(false)
            .build()
    }
}
