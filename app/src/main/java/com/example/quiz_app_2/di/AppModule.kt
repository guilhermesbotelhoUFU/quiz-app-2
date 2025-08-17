package com.example.quiz_app_2.di

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.quiz_app_2.data.local.QuestionDao
import com.example.quiz_app_2.data.local.QuizDatabase
import com.example.quiz_app_2.data.local.UserDao
import com.example.quiz_app_2.data.repository.AuthRepository
import com.example.quiz_app_2.data.repository.QuestionRepository
import com.example.quiz_app_2.data.repository.UserRepository
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(app: Application): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            "quiz_app_prefs",
            masterKeyAlias,
            app,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    fun provideQuizDatabase(app: Application): QuizDatabase {
        return QuizDatabase.getInstance(app)
    }

    @Provides
    @Singleton
    fun provideUserDao(database: QuizDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideQuestionDao(database: QuizDatabase): QuestionDao {
        return database.questionDao()
    }

    @Provides
    @Singleton
    fun provideRealtimeDatabase(): DatabaseReference {
        val database = FirebaseDatabase.getInstance()

        try {
            database.setPersistenceEnabled(true)
        } catch (e: Exception) {
            println("Persistência do Firebase já ativada: ${e.message}")
        }
        return database.reference
    }

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepository()
    }

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao, dbRef: DatabaseReference): UserRepository {
        return UserRepository(userDao, dbRef)
    }

    @Provides
    @Singleton
    fun provideQuestionRepository(
        dbRef: DatabaseReference,
        questionDao: QuestionDao,
        sharedPreferences: SharedPreferences
    ): QuestionRepository {
        return QuestionRepository(dbRef, questionDao, sharedPreferences)
    }
}