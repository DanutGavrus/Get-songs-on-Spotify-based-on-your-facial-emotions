package com.test.mande.data.api.dto

data class PayloadDTO(

    val box: List<Int> = arrayListOf(),

    val emotions: EmotionsDTO = EmotionsDTO()

)