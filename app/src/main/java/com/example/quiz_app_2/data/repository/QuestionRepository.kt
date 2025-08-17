package com.example.quiz_app_2.data.repository

import android.content.SharedPreferences
import com.example.quiz_app_2.data.local.QuestionDao
import com.example.quiz_app_2.data.model.Question
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

const val LOCAL_QUESTIONS_VERSION_KEY = "local_questions_version"

class QuestionRepository @Inject constructor(
    private val dbRef: DatabaseReference,
    private val questionDao: QuestionDao,
    private val sharedPreferences: SharedPreferences
) {
    suspend fun syncQuestionsIfNeeded() {
        try {
            val localVersion = sharedPreferences.getInt(LOCAL_QUESTIONS_VERSION_KEY, 0)
            val localQuestionCount = questionDao.getQuestionCount()
            val remoteVersionSnapshot = dbRef.child("metadata").child("questions_version").get().await()
            val remoteVersion = remoteVersionSnapshot.getValue(Int::class.java) ?: 1

            if (remoteVersion > localVersion || localQuestionCount == 0) {
                println("--> QuestionRepository: Sincronização NECESSÁRIA. Versão Remota: $remoteVersion, Versão Local: $localVersion, Contagem Local: $localQuestionCount")
                val questionsSnapshot = dbRef.child("perguntas").get().await()
                val allQuestions = mutableListOf<Question>()

                questionsSnapshot.children.forEach { themeSnapshot ->
                    themeSnapshot.children.forEach { difficultySnapshot ->
                        difficultySnapshot.children.forEach { questionData ->
                            questionData.getValue(Question::class.java)?.let {
                                allQuestions.add(it)
                            }
                        }
                    }
                }

                if (allQuestions.isNotEmpty()) {
                    questionDao.clearAll()
                    questionDao.insertAll(allQuestions)
                    sharedPreferences.edit().putInt(LOCAL_QUESTIONS_VERSION_KEY, remoteVersion).apply()
                    println("SINCRONIZAÇÃO DE PERGUNTAS COMPLETA: ${allQuestions.size} perguntas salvas.")
                }
            } else {
                println("Sincronização de perguntas não foi necessária. Versão local ($localVersion) está atualizada.")
            }
        } catch (e: Exception) {
            println("Falha ao sincronizar perguntas: ${e.message}")
        }
    }

    suspend fun getQuestions(theme: String, difficulty: String): List<Question> {
        println("--> QuestionRepository: Buscando no Room com Tema=$theme, Dificuldade=$difficulty")
        val questionsFromRoom = questionDao.getQuestions(theme, difficulty)
        println("--> QuestionRepository: Encontradas ${questionsFromRoom.size} perguntas no Room.")
        return questionsFromRoom
    }
}