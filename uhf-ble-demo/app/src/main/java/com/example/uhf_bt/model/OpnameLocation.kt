package com.example.uhf_bt.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OpnameLocation(
    @SerializedName("assrol_oid")
    val assrolOid: String?,

    @SerializedName("assrol_assro_oid")
    val assrolAssroOid: String?,

    @SerializedName("assrol_add_by")
    val assrolAddBy: String?,

    @SerializedName("assrol_add_date")
    val assrolAddDate: String?,

    @SerializedName("assrol_upd_by")
    val assrolUpdBy: String?,

    @SerializedName("assrol_upd_date")
    val assrolUpdDate: String?,

    @SerializedName("assrol_date")
    val assrolDate: String?,

    @SerializedName("assrol_loc_id")
    val assrolLocId: String?,

    @SerializedName("assrol_status")
    var assrolStatus: String? // Biasanya "O" (Open) atau "C" (Closed/Done)
) : Serializable