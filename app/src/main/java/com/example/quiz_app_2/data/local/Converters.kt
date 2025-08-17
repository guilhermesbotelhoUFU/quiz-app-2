package com.example.quiz_app_2.data.local

import androidx.room.TypeConverter
import com.example.quiz_app_2.data.model.GameResult
import com.example.quiz_app_2.data.model.ThemeStat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromGameResultList(value: List<GameResult>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toGameResultList(value: String): List<GameResult> {
        val listType = object : TypeToken<List<GameResult>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromThemeStatMap(value: Map<String, ThemeStat>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toThemeStatMap(value: String): Map<String, ThemeStat> {
        val mapType = object : TypeToken<Map<String, ThemeStat>>() {}.type
        return gson.fromJson(value, mapType)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
}