package com.chibidolls.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.chibidolls.app.core.audio.CuteSoundManager
import com.chibidolls.app.core.designsystem.ChibiDollsTheme
import com.chibidolls.app.core.designsystem.ScreenBackground
import com.chibidolls.app.core.repository.AppSettingsRepository
import com.chibidolls.app.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var soundManager: CuteSoundManager

    @Inject
    lateinit var appSettingsRepository: AppSettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChibiDollsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ScreenBackground
                ) {
                    val navController = rememberNavController()
                    AppNavGraph(navController = navController)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (appSettingsRepository.bgmEnabled.value) {
            soundManager.startBgm()
        }
    }

    override fun onPause() {
        super.onPause()
        soundManager.stopBgm()
    }
}
