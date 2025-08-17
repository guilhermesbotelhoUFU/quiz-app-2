package com.example.quiz_app_2.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

data class GameResult(
    val score: Int = 0,
    val theme: String = "",
    val difficulty: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val durationInSeconds: Int = 0
)

data class ThemeStat(
    val gamesPlayed: Int = 0,
    val correctAnswers: Int = 0,
    val totalQuestionsAnswered: Int = 0
)

data class UserStats(
    val totalGamesPlayed: Int = 0,
    val totalCorrectAnswers: Int = 0,
    val totalQuestionsAnswered: Int = 0,
    val statsByTheme: Map<String, ThemeStat> = emptyMap(),
    val recentGames: List<GameResult> = emptyList(),
    val lastUpdated: Long = System.currentTimeMillis(),
    val overallAccuracy: Double = 0.0
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val uid: String = "",
    val email: String = "",
    @Embedded val stats: UserStats = UserStats(),
    val isAdmin: Boolean = false
)