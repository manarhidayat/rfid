package com.example.uhf_bt.model

data class BaseResponse<T>(
    val status: Int,
    val data: T
)

data class ForeignResponse<T>(
    val status: Boolean,
    val data: T
)