package com.hao.data.model

data class Hut(
    val assetId: String,
    val name: String,
    val beds: Int,
    val facilities: List<String>
)
