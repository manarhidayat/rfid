package com.example.uhf_bt.model

import java.io.Serializable

data class OpnameLocation(
    val no: Int,
    val location: String,
    var status: String // "Open" atau "Done"
): Serializable