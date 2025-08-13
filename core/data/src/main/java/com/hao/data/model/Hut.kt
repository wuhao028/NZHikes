package com.hao.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "huts")
data class Hut(
    @PrimaryKey
    val assetId: String,
    val name: String,
    val beds: Int,
    val facilities: List<String>
)
