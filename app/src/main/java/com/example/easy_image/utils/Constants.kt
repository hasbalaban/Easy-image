package com.example.easy_image.utils


sealed class NavigationDirections(val route: String) {
    data object HomeScreen : NavigationDirections("homeScreen")
    data object Favorite : NavigationDirections("favoriteScreen")
    data object Video : NavigationDirections("videoScreen")
    data object SearchScreen : NavigationDirections("SearchScreen")
    data object DetailScreen: NavigationDirections("DetailScreen?imageUrl={imageUrl}&hash={hash}") {
        fun createRoute(imageUrl: String) = "DetailScreen?imageUrl=$imageUrl"
    }

}

data class NavDirections(
    val route : String,
    val arguments : String
)