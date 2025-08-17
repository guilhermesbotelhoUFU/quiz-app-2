package com.example.quiz_app_2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quiz_app_2.data.model.Question

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<Question>)

    @Query("SELECT * FROM questions_table WHERE tema = :theme AND dificuldade = :difficulty")
    suspend fun getQuestions(theme: String, difficulty: String): List<Question>

    @Query("SELECT COUNT(*) FROM questions_table")
    suspend fun getQuestionCount(): Int

    @Query("DELETE FROM questions_table")
    suspend fun clearAll()
}