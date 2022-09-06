package com.example.smartestmovieapp.data.local.model.movie_details

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*


class ListTypeConverter {

    private var gson = Gson()

    @TypeConverter
    fun stringToStringList(data: String?): List<String?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object : TypeToken<List<String?>?>() {}.type
        return gson.fromJson<List<String?>>(data, listType)
    }

    @TypeConverter
    fun stringListToString(stringList: List<String?>?): String? {
        return gson.toJson(stringList)
    }
}