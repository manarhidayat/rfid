package com.example.uhf_bt.model

data class BaseResponse<T>(
    val status: Int,
    val data: T
)