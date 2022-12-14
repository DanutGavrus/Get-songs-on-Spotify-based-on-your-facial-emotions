package com.test.mande.activity.home.viewModel

import android.widget.Button
import android.widget.TextView
import com.test.mande.data.api.dto.ImageDTO
import com.test.mande.data.api.dto.MoodDTO
import com.test.mande.data.database.repo.Repo

class HomeActivityVM(private val repo: Repo) {

    fun getDetectionResult(img: String, textView: TextView, retryBtn: Button, getSongBtn: Button, start: Long, detectEmotionBtn: Button) {

        repo.getDetectionResult(
            ImageDTO(
                img = img
            ),
            textView,
            retryBtn,
            getSongBtn,
            start,
            detectEmotionBtn
        )

    }

    fun getSongResult(mood: String, textView: TextView, getSongBtn: Button) {

        repo.getSongResult(
            MoodDTO(
                mood = mood
            ),
            textView,
            getSongBtn
        )

    }

}