package com.adeeba.plantdiseaseapp

object VideoRepository {

    fun getVideos(): List<VideoModel> {
        return listOf(

            VideoModel(
                videoId = "yJSHkTBvE8I",
                thumbnail = "https://img.youtube.com/vi/yJSHkTBvE8I/hqdefault.jpg",
                duration = "0:50"
            ),

            VideoModel(
                videoId = "MgnswLv0v5o",
                thumbnail = "https://img.youtube.com/vi/MgnswLv0v5o/hqdefault.jpg",
                duration = "0:14"
            ),

            VideoModel(
                videoId = "q0D0sFDj4MM",
                thumbnail = "https://img.youtube.com/vi/q0D0sFDj4MM/hqdefault.jpg",
                duration = "0:20"
            ),

            VideoModel(
                videoId = "fvZV_N5innE",
                thumbnail = "https://img.youtube.com/vi/fvZV_N5innE/hqdefault.jpg",
                duration = "0:30"
            ),

            VideoModel(
                videoId = "BvCySGTm0sc",
                thumbnail = "https://img.youtube.com/vi/BvCySGTm0sc/hqdefault.jpg",
                duration = "0:18"
            )
        )
    }
}