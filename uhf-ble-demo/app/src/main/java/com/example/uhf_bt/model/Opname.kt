package com.example.uhf_bt.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Opname(
    @SerializedName("assro_oid")
    val assroOid: String?,

    @SerializedName("assro_en_id")
    val assroEnId: String?,

    @SerializedName("assro_add_by")
    val assroAddBy: String?,

    @SerializedName("assro_add_date")
    val assroAddDate: String?,

    @SerializedName("assro_upd_by")
    val assroUpdBy: String?,

    @SerializedName("assro_upd_date")
    val assroUpdDate: String?,

    @SerializedName("assro_code")
    val assroCode: String?,

    @SerializedName("assro_desc")
    val assroDesc: String?,

    @SerializedName("assro_start_date")
    val assroStartDate: String?,

    @SerializedName("assro_end_date")
    val assroEndDate: String?,

    @SerializedName("assro_status")
    val assroStatus: String?,

    @SerializedName("assro_dt")
    val assroDt: String?
) : Serializable