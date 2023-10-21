package com.example.easy_image.model

data class Favorites(
    val imageList: ArrayList<FavoriteDTO>
)

data class FavoriteDTO(
    val uuId : Long,
    val imageUrl : String
)

object Favorite{
    val favoriteImageList : Favorites = Favorites(ArrayList())

    fun addOrRemoveFavorite(item : FavoriteDTO): Boolean {
        return if (favoriteImageList.imageList.contains(item)) {
            favoriteImageList.imageList.remove(item)
            false
        }
        else {
            favoriteImageList.imageList.add(item)
            true
        }

    }

}


