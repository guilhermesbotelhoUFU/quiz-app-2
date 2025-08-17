package com.example.quiz_app_2.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.quiz_app_2.data.model.UserProfile

@Dao
interface UserDao {

    @Upsert
    suspend fun upsertUser(user: UserProfile)

    @Query("SELECT * FROM user_profile WHERE uid = :uid")
    suspend fun getUser(uid: String): UserProfile?

    @Query("DELETE FROM user_profile WHERE uid = :uid")
    suspend fun deleteUser(uid: String)
}