package com.hao.data.data.converter

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * Type converter for storing/retrieving List<List<List<Double>>> in Room
 */
class CoordinateListConverter {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val type = Types.newParameterizedType(
        List::class.java,
        Types.newParameterizedType(
            List::class.java,
            Types.newParameterizedType(List::class.java, Double::class.javaObjectType)
        )
    )

    private val adapter = moshi.adapter<List<List<List<Double>>>>(type)

    @TypeConverter
    fun fromString(value: String): List<List<List<Double>>> {
        return adapter.fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun toString(list: List<List<List<Double>>>): String {
        return adapter.toJson(list)
    }
}
