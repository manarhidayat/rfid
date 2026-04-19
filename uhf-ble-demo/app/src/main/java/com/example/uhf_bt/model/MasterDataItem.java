package com.example.uhf_bt.model;

import com.google.gson.annotations.SerializedName;

public class MasterDataItem {
        @SerializedName("ass_old")
        private String assOld;
        
        @SerializedName("epclist")
        private String epcList;
        
        @SerializedName("ass_dom_id")
        private String assDomId;
        
        @SerializedName("ass_en_id")
        private String assEnId;
        
        @SerializedName("en_desc")
        private String enDesc;
        
        @SerializedName("ass_branch_id")
        private String assBranchId;
        
        @SerializedName("branch_name")
        private String branchName;
        
        @SerializedName("ass_add_by")
        private String assAddBy;
        
        @SerializedName("ass_add_date")
        private String assAddDate;
        
        @SerializedName("ass_upd_by")
        private String assUpdBy;
        
        @SerializedName("ass_upd_date")
        private String assUpdDate;
        
        @SerializedName("ass_id")
        private String assId;
        
        @SerializedName("ass_pt_id")
        private String assPtId;

        @SerializedName("pt_code")
        private String ptCode;
        
        @SerializedName("pt_desc1")
        private String ptDesc;
        
        @SerializedName("pt_desc2")
        private String ptDesc2;
        
        @SerializedName("ass_code")
        private String assCode;
        
        @SerializedName("ass_qr_code")
        private String assQrCode;
        
        @SerializedName("ass_rfid_code")
        private String assRfidCode;
        
        @SerializedName("ass_desc")
        private String assDesc;

        @SerializedName("ass_service_date")
        private String assServiceDate;

        @SerializedName("ass_ref_po")
        private String assRefPo;

        @SerializedName("ass_ref_rcpt")
        private String assRefRcpt;
        
        // Getter methods
        public String getAssOld() {
            return assOld;
        }
        
        public String getEpcList() {
            return epcList;
        }
        
        public String getAssDomId() {
            return assDomId;
        }
        
        public String getAssEnId() {
            return assEnId;
        }
        
        public String getAssServiceDate() {
            return assServiceDate;
        }
        
        public String getAssRefPo() {
            return assRefPo;
        }
        
        public String getAssRefRcpt() {
            return assRefRcpt;
        }
        
        public String getEnDesc() {
            return enDesc;
        }
        
        public String getAssBranchId() {
            return assBranchId;
        }
        
        public String getBranchName() {
            return branchName;
        }
        
        public String getAssAddBy() {
            return assAddBy;
        }
        
        public String getAssAddDate() {
            return assAddDate;
        }
        
        public String getAssUpdBy() {
            return assUpdBy;
        }
        
        public String getAssUpdDate() {
            return assUpdDate;
        }
        
        public String getAssId() {
            return assId;
        }
        
        public String getAssPtId() {
            return assPtId;
        }

        public String getPtCode() {
            return ptCode;
        }
        
        public String getPtDesc() {
            return ptDesc;
        }
        
        public String getPtDesc2() {
            return ptDesc2;
        }
        
        public String getAssCode() {
            return assCode;
        }
        
        public String getAssQrCode() {
            return assQrCode;
        }
        
        public String getAssRfidCode() {
            return assRfidCode;
        }
        
        public String getAssDesc() {
            return assDesc;
        }
}