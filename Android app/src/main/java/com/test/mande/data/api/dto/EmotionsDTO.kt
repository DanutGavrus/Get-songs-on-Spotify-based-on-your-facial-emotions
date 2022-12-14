package com.test.mande.data.api.dto

import kotlin.math.floor

data class EmotionsDTO(

    val angry: Double = 0.0,

    val disgust: Double = 0.0,

    val fear: Double = 0.0,

    val happy: Double = 0.0,

    val neutral: Double = 0.0,

    val sad: Double = 0.0,

    val surprise: Double = 0.0

) {

    var others = 0.0

    override fun toString(): String {
        var string = ""
        if (happy > 0) string += " happy " + floor(happy * 100) + "%,"
        if (sad > 0) string += " sad " + floor(sad * 100) + "%,"
        if (angry > 0) string += " angry " + floor(angry * 100) + "%,"
        if (neutral > 0) string += " neutral " + floor(neutral * 100) + "%,"
        others = 1 - happy - sad - angry - neutral
        if (others > 0) string += " others " + floor(others * 100) + "%,"
//        if (surprise > 0) string += " surprise " + floor(surprise*100) + "%,"
//        if (disgust > 0) string += " disgust " + floor(disgust*100) + "%,"
//        if (fear > 0) string += " fear " + floor(fear*100) + "%,"
        string = string.substring(0, string.length - 1) + "."
        return string
    }

    fun predominantEmotion(): String {
        val emotionsArray = arrayOf(happy, sad, angry, neutral, others)
        return when (emotionsArray.maxOrNull()){
            happy -> {
                "Happy"
            }
            sad -> {
                "Sad"
            }
            angry -> {
                "Angry"
            }
            neutral -> {
                "Neutral"
            }
            others -> {
                "Others"
            }
            else -> {
                "Error"
            }
        }
    }

}