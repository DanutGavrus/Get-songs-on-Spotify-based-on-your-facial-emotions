package com.test.mande.data.database.repo.impl.general

import android.annotation.SuppressLint
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.test.mande.activity.home.HomeActivity
import com.test.mande.data.api.dto.DetectionResultDTO
import com.test.mande.data.api.dto.ImageDTO
import com.test.mande.data.api.dto.MoodDTO
import com.test.mande.data.api.dto.SongResultDTO
import com.test.mande.data.api.service.Service
import com.test.mande.data.database.repo.Repo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoImpl(private val service: Service) : Repo {

    @SuppressLint("SetTextI18n")
    override fun getDetectionResult(imageDTO: ImageDTO, textView: TextView, retryBtn: Button, getSongBtn: Button, start: Long, detectEmotionBtn: Button) {
        service
            .postDetectEmotions(imageDTO)
            .enqueue(object : Callback<DetectionResultDTO> {
                override fun onFailure(call: Call<DetectionResultDTO>, t: Throwable) {
                    textView.text = "Result: failed. Could not connect to the server."
                    t.printStackTrace()
                    detectEmotionBtn.visibility = View.INVISIBLE
                    retryBtn.visibility = View.VISIBLE
                    retryBtn.isEnabled = true
                }

                override fun onResponse(call: Call<DetectionResultDTO>, response: Response<DetectionResultDTO>) {
                    detectEmotionBtn.visibility = View.INVISIBLE
                    retryBtn.visibility = View.VISIBLE
                    retryBtn.isEnabled = true

                    response.body()?.payload?.also {
                        if (it.isNotEmpty()) {
                            textView.text = "Result:" + it[0].emotions.toString()
                            HomeActivity.predominantEmotion = it[0].emotions.predominantEmotion()
                            getSongBtn.visibility = View.VISIBLE
                            getSongBtn.isEnabled = true
                            /*val end = System.currentTimeMillis()
                            println("TIME AAAAAA ${(end - start)} ms")*/
                        } else {
                            textView.text = "Result: failed. Make sure your face is clearly visible and the picture is steady."
                        }
                    }
                    if (response.body()?.payload == null) {
                        textView.text = "Result: failed. Make sure your face is clearly visible and the picture is steady."
                    }
                }
            })
    }

    override fun getSongResult(moodDTO: MoodDTO, textView: TextView, getSongBtn: Button) {
        service
            .postDetectSongs(moodDTO)
            .enqueue(object : Callback<SongResultDTO> {
                override fun onFailure(call: Call<SongResultDTO>, t: Throwable) {
                    getSongBtn.isEnabled = true
                    t.printStackTrace()
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<SongResultDTO>, response: Response<SongResultDTO> ) {
                    getSongBtn.isEnabled = true

                    response.body()?.also {
                        textView.text = "Result for ${HomeActivity.predominantEmotion}: http://open.spotify.com/track/" + it.id
                    }
                }
            })
    }

}