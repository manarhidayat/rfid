package com.example.uhf_bt.model

import com.google.gson.annotations.SerializedName

data class NextCodeResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("assro_code")
    val assroCode: String
)
