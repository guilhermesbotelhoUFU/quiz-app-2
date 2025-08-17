package com.example.quiz_app_2.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions_table")
data class Question(
    @PrimaryKey val id: String = "",
    val pergunta: String = "",
    val opcoes: List<String> = emptyList(),
    val respostaCorreta: String = "",
    val tema: String = "",
    val dificuldade: String = ""
)