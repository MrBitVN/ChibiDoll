package com.chibidolls.app.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object CharacterEditor : Screen("editor?characterId={characterId}") {
        fun createRoute(characterId: String? = null): String {
            return if (characterId != null) "editor?characterId=$characterId" else "editor"
        }
    }
    data object SceneEditor : Screen("scene?sceneId={sceneId}") {
        fun createRoute(sceneId: String? = null): String {
            return if (sceneId != null) "scene?sceneId=$sceneId" else "scene"
        }
    }
    data object Collection : Screen("collection")
    data object Settings : Screen("settings")
}
