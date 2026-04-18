package com.example.uhf_bt.model

import java.io.Serializable

data class AssetOpname(
    val no: Int,
    val assetCode: String,
    val assetName: String,
    var status: String // "Found", "Not Found", "Foreign"
): Serializable