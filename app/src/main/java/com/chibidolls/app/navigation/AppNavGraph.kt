package com.chibidolls.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.chibidolls.app.features.collection.CollectionScreen
import com.chibidolls.app.features.editor.CharacterEditorScreen
import com.chibidolls.app.features.home.HomeScreen
import com.chibidolls.app.features.scene.SceneEditorScreen
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
                onCreateDollClick = {
                    navController.navigate(Screen.CharacterEditor.createRoute())
                },
                onSceneStudioClick = {
                    navController.navigate(Screen.SceneEditor.createRoute())
                },
                onCollectionClick = {
                    navController.navigate(Screen.Collection.route)
                },
                onRandomClick = {
                    navController.navigate(Screen.CharacterEditor.createRoute())
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.CharacterEditor.route,
            arguments = listOf(
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
                    navController.navigate(Screen.Collection.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(
            route = Screen.SceneEditor.route,
            arguments = listOf(
                navArgument("sceneId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            SceneEditorScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Collection.route) {
            CollectionScreen(
                onBackClick = { navController.popBackStack() },
                onDollClick = { dollId ->
                    navController.navigate(Screen.CharacterEditor.createRoute(dollId))
                },
                onAddNewClick = {
                    navController.navigate(Screen.CharacterEditor.createRoute())
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
