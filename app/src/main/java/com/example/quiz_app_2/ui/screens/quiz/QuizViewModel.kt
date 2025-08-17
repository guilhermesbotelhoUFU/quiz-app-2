package com.example.quiz_app_2.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz_app_2.data.model.GameResult
import com.example.quiz_app_2.data.model.Question
import com.example.quiz_app_2.data.model.ThemeStat
import com.example.quiz_app_2.data.repository.AuthRepository
import com.example.quiz_app_2.data.repository.QuestionRepository
import com.example.quiz_app_2.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val isLoading: Boolean = true,
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswer: String? = null,
    val isAnswerCorrect: Boolean? = null,
    val score: Int = 0,
    val correctAnswersCount: Int = 0,
    val timeLeft: Int = 90,
    val quizFinished: Boolean = false
) {
    val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex)
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val questionRepository: QuestionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState = _uiState.asStateFlow()

    private var quizTheme: String = ""
    private var quizDifficulty: String = ""

    fun startQuiz(tema: String, dificuldade: String) {
        quizTheme = tema
        quizDifficulty = dificuldade
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            println("--> QuizViewModel: Carregando perguntas para Tema=$quizTheme, Dificuldade=$quizDifficulty")
            val questions = questionRepository.getQuestions(quizTheme, quizDifficulty)
            println("--> QuizViewModel: Repositório retornou ${questions.size} perguntas.")
            val originalQuestions = questionRepository.getQuestions(quizTheme, quizDifficulty)

            val shuffledQuestions = originalQuestions.shuffled().take(10)

            val questionsWithShuffledOptions = shuffledQuestions.map { question ->
                question.copy(opcoes = question.opcoes.shuffled())
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    questions = questionsWithShuffledOptions
                )
            }
            startTimer()
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (_uiState.value.timeLeft > 0 && !_uiState.value.quizFinished) {
                delay(1000)
                _uiState.update { it.copy(timeLeft = it.timeLeft - 1) }
            }
            if (!_uiState.value.quizFinished) {
                finishQuiz()
            }
        }
    }

    fun selectAnswer(answer: String) {
        if (_uiState.value.isAnswerCorrect == null) {
            _uiState.update { it.copy(selectedAnswer = answer) }
        }
    }

    fun confirmAnswer() {
        val state = _uiState.value
        val currentQuestion = state.currentQuestion ?: return
        val selectedAnswer = state.selectedAnswer ?: return

        val isCorrect = selectedAnswer == currentQuestion.respostaCorreta
        val newScore = if (isCorrect) state.score + 10 else state.score
        val newCorrectAnswersCount = if (isCorrect) state.correctAnswersCount + 1 else state.correctAnswersCount

        _uiState.update {
            it.copy(
                isAnswerCorrect = isCorrect,
                score = newScore,
                correctAnswersCount = newCorrectAnswersCount
            )
        }

        viewModelScope.launch {
            delay(1500)
            nextQuestion()
        }
    }

    private fun nextQuestion() {
        val state = _uiState.value
        if (state.currentQuestionIndex < state.questions.size - 1) {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    selectedAnswer = null,
                    isAnswerCorrect = null
                )
            }
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        if (_uiState.value.quizFinished) return
        _uiState.update { it.copy(quizFinished = true) }

        viewModelScope.launch {
            val uid = authRepository.getCurrentUser()?.uid ?: return@launch
            val userProfile = userRepository.getUser(uid) ?: return@launch
            val currentState = _uiState.value
            val totalQuestionsInQuiz = currentState.questions.size
            val totalTime = 90
            val elapsedTime = totalTime - currentState.timeLeft

            val newResult = GameResult(
                score = currentState.score,
                theme = quizTheme,
                difficulty = quizDifficulty,
                durationInSeconds = elapsedTime
            )

            val stats = userProfile.stats
            val updatedTotalGames = stats.totalGamesPlayed + 1
            val updatedTotalCorrects = stats.totalCorrectAnswers + currentState.correctAnswersCount
            val updatedTotalAnswered = stats.totalQuestionsAnswered + totalQuestionsInQuiz

            val updatedAccuracy = if (updatedTotalAnswered > 0) {
                updatedTotalCorrects.toDouble() / updatedTotalAnswered
            } else 0.0

            val themeStats = stats.statsByTheme[quizTheme] ?: ThemeStat()
            val updatedThemeStat = themeStats.copy(
                gamesPlayed = themeStats.gamesPlayed + 1,
                correctAnswers = themeStats.correctAnswers + currentState.correctAnswersCount,
                totalQuestionsAnswered = themeStats.totalQuestionsAnswered + totalQuestionsInQuiz
            )
            val updatedStatsByTheme = stats.statsByTheme.toMutableMap()
            updatedStatsByTheme[quizTheme] = updatedThemeStat

            val updatedRecentGames = (listOf(newResult) + stats.recentGames).take(5)

            val updatedProfile = userProfile.copy(
                stats = stats.copy(
                    totalGamesPlayed = updatedTotalGames,
                    totalCorrectAnswers = updatedTotalCorrects,
                    totalQuestionsAnswered = updatedTotalAnswered,
                    statsByTheme = updatedStatsByTheme,
                    recentGames = updatedRecentGames,
                    lastUpdated = System.currentTimeMillis(),
                    overallAccuracy = updatedAccuracy
                )
            )
            userRepository.saveUser(updatedProfile)
        }
    }
}