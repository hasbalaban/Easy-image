package com.easyImage.data.model

import com.example.easy_image.NavigationDirections

enum class ScreenType(route: String) {
    Home(NavigationDirections.HomeScreen.route), Favorite(NavigationDirections.Favorite.route), Video(
        NavigationDirections.Video.route,
    )
}