package com.hao.nzhikes.data.converter

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * Type converter for storing/retrieving List<String> in Room
 */
class StringListConverter {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val type = Types.newParameterizedType(List::class.java, String::class.java)
    private val adapter = moshi.adapter<List<String>>(type)

    @TypeConverter
    fun fromString(value: String): List<String> {
        return adapter.fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun toString(list: List<String>): String {
        return adapter.toJson(list)
    }
}
