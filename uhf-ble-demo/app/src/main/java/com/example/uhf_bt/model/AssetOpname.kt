package com.example.uhf_bt.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AssetOpname(
    @SerializedName("ass_oid")
    val assOid: String?,

    @SerializedName("ass_dom_id")
    val assDomId: String?,

    @SerializedName("ass_en_id")
    val assEnId: String?,

    @SerializedName("ass_add_by")
    val assAddBy: String?,

    @SerializedName("ass_add_date")
    val assAddDate: String?,

    @SerializedName("ass_upd_by")
    val assUpdBy: String?,

    @SerializedName("ass_upd_date")
    val assUpdDate: String?,

    @SerializedName("ass_id")
    val assId: String?,

    @SerializedName("ass_pt_id")
    val assPtId: String?,

    @SerializedName("ass_code")
    val assCode: String?,

    @SerializedName("ass_barcode")
    val assBarcode: String?,

    @SerializedName("ass_desc")
    val assDesc: String?,

    @SerializedName("ass_qty")
    val assQty: String?,

    @SerializedName("ass_sn")
    val assSn: String?,

    @SerializedName("ass_service_date")
    val assServiceDate: String?,

    @SerializedName("ass_emp_id")
    val assEmpId: String?,

    @SerializedName("ass_remarks")
    val assRemarks: String?,

    @SerializedName("ass_loc_id")
    val assLocId: String?,

    @SerializedName("ass_rfid_code")
    val assRfidCode: String?,

    @SerializedName("ass_opname_note")
    var assOpnameNote: String?,

    // Field tambahan untuk kebutuhan UI (Status scanning)
    // Default-nya bisa kita set "Not Found" atau sesuai logika aplikasi Anda
    var status: String = "Not Found"
) : Serializable