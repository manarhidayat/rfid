package com.example.uhf_bt.model

import com.google.gson.annotations.SerializedName

data class OpnameSubmitItem(
    @SerializedName("assrop_assro_oid")
    val assroOid: String?,
    @SerializedName("assrop_assrol_oid")
    val assrolOid: String?,
    @SerializedName("assrop_ass_id")
    val assId: String?,
    @SerializedName("assrop_status")
    val status: String
)