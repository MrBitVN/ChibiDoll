package com.chibidolls.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.chibidolls.app.features.album.AlbumScreen
import com.chibidolls.app.features.editor.CharacterEditorScreen
import com.chibidolls.app.features.home.HomeScreen
import com.chibidolls.app.features.room.RoomScreen
import com.chibidolls.app.features.settings.SettingsScreen
import com.chibidolls.app.features.splash.SplashScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onReady = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onPlayClick = {
                    navController.navigate(Screen.Room.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Room.route) {
            RoomScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onAlbumClick = {
                    navController.navigate(Screen.Album.route)
                },
                onOpenEditorForSlot = { slotIndex, characterId ->
                    navController.navigate(Screen.CharacterEditor.createRoute(slotIndex, characterId))
                }
            )
        }

        composable(
            route = Screen.CharacterEditor.route,
            arguments = listOf(
                navArgument("slotIndex") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "0"
                },
                navArgument("characterId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            CharacterEditorScreen(
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Album.route) {
            AlbumScreen(
                onBackClick = { navController.popBackStack() },
                onTakePhotoClick = { navController.popBackStack() },
                onEditPhotoClick = { photo ->
                    navController.navigate(Screen.CharacterEditor.createRoute(0, null))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
