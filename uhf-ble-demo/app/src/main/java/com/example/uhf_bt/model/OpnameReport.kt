import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OpnameReport(
    @SerializedName("opname") val opname: OpnameData?,
    @SerializedName("location") val location: OpnameReportLocation?,
    @SerializedName("item") val item: OpnameItem?
) : Serializable

data class OpnameData(
    @SerializedName("assro_oid") val assroOid: String?,
    @SerializedName("assro_en_id") val assroEnId: String?,
    @SerializedName("assro_code") val assroCode: String?,
    @SerializedName("assro_desc") val assroDesc: String?,
    @SerializedName("assro_start_date") val assroStartDate: String?,
    @SerializedName("assro_end_date") val assroEndDate: String?,
    @SerializedName("assro_status") val assroStatus: String?,
    @SerializedName("assro_add_by") val assroAddBy: String?,
    @SerializedName("assro_add_date") val assroAddDate: String?,
    @SerializedName("assro_upd_by") val assroUpdBy: String?,
    @SerializedName("assro_upd_date") val assroUpdDate: String?
) : Serializable

data class OpnameReportLocation(
    @SerializedName("assrol_oid") val assrolOid: String?,
    @SerializedName("assrol_assro_oid") val assrolAssroOid: String?,
    @SerializedName("assrol_loc_id") val assrolLocId: String?,
    @SerializedName("assrol_date") val assrolDate: String?,
    @SerializedName("assrol_status") val assrolStatus: String?,
    @SerializedName("assrol_add_by") val assrolAddBy: String?,
    @SerializedName("assrol_add_date") val assrolAddDate: String?,
    @SerializedName("assrol_upd_by") val assrolUpdBy: String?,
    @SerializedName("assrol_upd_date") val assrolUpdDate: String?,
    @SerializedName("loc_code") val locCode: String?,
    @SerializedName("loc_desc") val locDesc: String?,
    @SerializedName("loc_type") val locType: String?
) : Serializable

data class OpnameItem(
    @SerializedName("assrop_oid") val assropOid: String?,
    @SerializedName("assrop_assro_oid") val assropAssroOid: String?,
    @SerializedName("assrop_assrol_oid") val assropAssrolOid: String?,
    @SerializedName("assrop_ass_id") val assropAssId: String?,
    @SerializedName("assrop_date") val assropDate: String?,
    @SerializedName("assrop_qty") val assropQty: String?,
    @SerializedName("assrop_status") val assropStatus: String?,
    @SerializedName("assrop_foreign_loc") val assropForeignLoc: String?,
    @SerializedName("assrop_add_by") val assropAddBy: String?,
    @SerializedName("assrop_add_date") val assropAddDate: String?,
    @SerializedName("assrop_upd_by") val assropUpdBy: String?,
    @SerializedName("assrop_upd_date") val assropUpdDate: String?,
    @SerializedName("ass_code") val assCode: String?,
    @SerializedName("ass_desc") val assDesc: String?,
    @SerializedName("ass_sn") val assSn: String?,
    @SerializedName("ass_barcode") val assBarcode: String?,
    @SerializedName("ass_rfid_code") val assRfidCode: String?,
    @SerializedName("pt_code") val ptCode: String?,
    @SerializedName("pt_desc1") val ptDesc1: String?,
    @SerializedName("pt_desc2") val ptDesc2: String?,
    @SerializedName("foreign_loc_code") val foreignLocCode: String?,
    @SerializedName("foreign_loc_desc") val foreignLocDesc: String?,
    @SerializedName("previous_location") val previousLocation: String?,
    @SerializedName("category") val category: String?
) : Serializable