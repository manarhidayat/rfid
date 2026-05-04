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
    var assrolStatus: String?,

    // Field tambahan sesuai JSON baru
    @SerializedName("loc_id")
    val locId: String?,

    @SerializedName("loc_code")
    val locCode: String?,

    @SerializedName("loc_desc")
    val locDesc: String?,

    @SerializedName("loc_type")
    val locType: String?
) : Serializable