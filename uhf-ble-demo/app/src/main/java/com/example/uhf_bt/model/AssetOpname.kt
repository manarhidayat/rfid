package com.example.uhf_bt.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AssetOpname(
    @SerializedName("ass_id")
    val assId: String?,

    @SerializedName("ass_oid")
    val assOid: String?,

    @SerializedName("ass_code")
    val assCode: String?,

    @SerializedName("ass_desc")
    val assDesc: String?,

    @SerializedName("ass_sn")
    val assSn: String?,

    @SerializedName("ass_barcode")
    val assBarcode: String?,

    @SerializedName("ass_rfid_code")
    val assRfidCode: String?,

    @SerializedName("ass_qty")
    val assQty: String?,

    @SerializedName("ass_loc_id")
    val assLocId: String?,

    @SerializedName("pt_code")
    val ptCode: String?,

    @SerializedName("pt_desc1")
    val ptDesc1: String?,

    @SerializedName("pt_desc2")
    val ptDesc2: String?,

    @SerializedName("assrop_oid")
    val assropOid: String?,

    @SerializedName("assrop_assro_oid")
    val assropAssroOid: String?,

    @SerializedName("assrop_assrol_oid")
    val assropAssrolOid: String?,

    @SerializedName("assrop_status")
    var status: String?,

    @SerializedName("assrop_date")
    val assropDate: String?,

    @SerializedName("assrop_qty")
    val assropQty: String?,

    @SerializedName("assrop_foreign_loc")
    val assropForeignLoc: String?,

    @SerializedName("assrop_is_input")
    val assropIsInput: String?,

    // Field tambahan untuk kebutuhan UI (Status scanning)
    // Kita gunakan assropStatus sebagai acuan utama,
    // tapi variabel 'status' ini bisa tetap ada jika adapter Anda menggunakannya
//    var status: String = "2" // Default "2" (Not Found) sesuai logika sebelumnya
) : Serializable