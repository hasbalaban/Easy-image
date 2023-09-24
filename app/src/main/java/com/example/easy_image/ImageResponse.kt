package com.example.easy_image

data class ImageResponse (
    val total : String?,
    val totalHits : String?,
    val hits : List<Hits>?
        )

data class Hits (
    val id : Int,
    val pageURL : String?,
    val type : String?,
    val tags : String?,
    val previewURL : String?,
    val previewWidth : Int?,
    val previewHeight : Int?,
    val webformatURL : String?,
    val webformatWidth : Int?,
    val webformatHeight : Int?,
    val largeImageURL : String?,
    val fullHDURL : String?,
    val imageURL : String?,
    val imageWidth : String?,
    val imageHeight : String?,
    val imageSize : String?,
    val views : String?,
    val downloads : String?,
    val collections : Int?,
    val likes : String?,
    val comments : String?,
    val user_id : String?,
    val user : String?,
    val userImageURL : String?)
{
  var uuId : Long = 0L
}