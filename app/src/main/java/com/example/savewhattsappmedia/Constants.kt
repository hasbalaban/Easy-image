package com.example.savewhattsappmedia

object Constants {
    val firstNumber = 1
}

sealed class NavigationDirections(val route: String) {
    object HomeScreen : NavigationDirections("homeScreen")
    object SearchScreen : NavigationDirections("SearchScreen")
    object DetailScreen: NavigationDirections("DetailScreen?imageUrl={imageUrl}") {
        fun createRoute(imageUrl: String) = "DetailScreen?imageUrl=$imageUrl"
    }

}

data class NavDirections(
    val route : String,
    val arguments : String
)