package com.test.mande.data.database.repo

import android.widget.Button
import android.widget.TextView
import com.test.mande.data.api.dto.ImageDTO
import com.test.mande.data.api.dto.MoodDTO

interface Repo {

    fun getDetectionResult(imageDTO: ImageDTO, textView: TextView, retryBtn: Button, getSongBtn: Button, start: Long, detectEmotionBtn: Button)

    fun getSongResult(moodDTO: MoodDTO, textView: TextView, getSongBtn: Button)

}