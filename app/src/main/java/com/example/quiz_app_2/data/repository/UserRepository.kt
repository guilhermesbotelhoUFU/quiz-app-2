package com.example.quiz_app_2.data.repository

import com.example.quiz_app_2.data.local.UserDao
import com.example.quiz_app_2.data.model.UserProfile
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val dbRef: DatabaseReference
) {

    suspend fun getUser(uid: String): UserProfile? {
        return try {
            val userProfileFromFirebase = getUserFromFirebase(uid)
            if (userProfileFromFirebase != null) {
                userDao.upsertUser(userProfileFromFirebase)
                userProfileFromFirebase
            } else {
                getUserFromRoom(uid)
            }
        } catch (e: Exception) {
            println("Erro ao buscar do Firebase: ${e.message}. Usando dados locais.")
            getUserFromRoom(uid)
        }
    }

    suspend fun getUserFromFirebase(uid: String): UserProfile? {
        return try {
            val snapshot = dbRef.child("users").child(uid).get().await()
            snapshot.getValue(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserFromRoom(uid: String): UserProfile? {
        return userDao.getUser(uid)
    }

    suspend fun saveUser(user: UserProfile) {
        userDao.upsertUser(user)
        dbRef.child("users").child(user.uid).setValue(user).await()
    }

    suspend fun getTopRankedUsers(): List<UserProfile> {
        return try {
            val snapshot = dbRef.child("users")
                .orderByChild("stats/overallAccuracy")
                .limitToLast(5)
                .get().await()

            val topUsers = snapshot.children.mapNotNull {
                it.getValue(UserProfile::class.java)
            }

            topUsers.reversed()
        } catch (e: Exception) {
            println("Erro ao buscar ranking: ${e.message}")
            emptyList()
        }
    }
}