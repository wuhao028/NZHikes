package com.hao.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "campsites")
data class Campsite(
    @PrimaryKey
    val assetId: String,
    val name: String,
    val facilities: List<String>
)
