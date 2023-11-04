package com.example.easy_image.utils


sealed class NavigationDirections(val route: String) {
    object HomeScreen : NavigationDirections("homeScreen")
    object Favorite : NavigationDirections("favoriteScreen")
    object Video : NavigationDirections("videoScreen")
    object SearchScreen : NavigationDirections("SearchScreen")
    object DetailScreen: NavigationDirections("DetailScreen?imageUrl={imageUrl}&hash={hash}") {
        fun createRoute(imageUrl: String) = "DetailScreen?imageUrl=$imageUrl"
    }

}

data class NavDirections(
    val route : String,
    val arguments : String
)