package com.hao.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "campsites")
data class Campsite(
    @PrimaryKey
    val assetId: String,
    val name: String? = "",
    val status: String? = "",
    val region: String? = "",
    val y: Double = 0.0,
    val x: Double = 0.0
)
