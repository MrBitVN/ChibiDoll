package com.chibidolls.app.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Room : Screen("room")
    data object CharacterEditor : Screen("editor?slotIndex={slotIndex}&characterId={characterId}") {
        fun createRoute(slotIndex: Int = 0, characterId: String? = null): String {
            return if (characterId != null) {
                "editor?slotIndex=$slotIndex&characterId=$characterId"
            } else {
                "editor?slotIndex=$slotIndex"
            }
        }
    }
    data object Album : Screen("album")
    data object Settings : Screen("settings")
}
