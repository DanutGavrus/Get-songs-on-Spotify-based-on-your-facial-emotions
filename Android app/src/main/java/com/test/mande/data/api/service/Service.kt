package com.test.mande.data.api.service

import com.test.mande.data.api.dto.DetectionResultDTO
import com.test.mande.data.api.dto.ImageDTO
import com.test.mande.data.api.dto.MoodDTO
import com.test.mande.data.api.dto.SongResultDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface Service {

    @POST("/image")
    fun postDetectEmotions(@Body imageDTO: ImageDTO): Call<DetectionResultDTO>

    @POST("/mood")
    fun postDetectSongs(@Body moodDTO: MoodDTO): Call<SongResultDTO>

}