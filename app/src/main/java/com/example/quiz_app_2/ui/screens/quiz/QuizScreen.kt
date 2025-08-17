package com.example.quiz_app_2.ui.screens.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.quiz_app_2.ui.theme.Quizapp2Theme
import com.example.quiz_app_2.ui.theme.RoxoEscuro
import com.example.quiz_app_2.ui.theme.VerdeLimao

@Composable
fun QuizScreen(
    navController: NavController,
    tema: String,
    dificuldade: String,
    quizViewModel: QuizViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = Unit) {
        quizViewModel.startQuiz(tema, dificuldade)
    }

    val uiState by quizViewModel.uiState.collectAsState()
    val totalTime = 90
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = !uiState.quizFinished) {
        showExitDialog = true
    }

    if (showExitDialog) {
        ExitQuizDialog(
            onConfirm = {
                showExitDialog = false
                navController.popBackStack()
            },
            onDismiss = {
                showExitDialog = false
            }
        )
    }

    Surface(modifier = Modifier.fillMaxSize(), color = RoxoEscuro) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VerdeLimao)
                }
            }
            uiState.quizFinished -> {
                val tempoDecorrido = totalTime - uiState.timeLeft
                val minutos = tempoDecorrido / 60
                val segundos = tempoDecorrido % 60
                val tempoFormatado = String.format("%02d:%02d", minutos, segundos)

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Quiz Finalizado!", style = MaterialTheme.typography.headlineLarge, color = VerdeLimao)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Sua pontuação: ${uiState.score}", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Seu tempo: $tempoFormatado", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Voltar ao Menu")
                    }
                }
            }
            else -> {
                uiState.currentQuestion?.let { pergunta ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        TopBar(
                            tempoRestanteEmSegundos = uiState.timeLeft,
                            tempoTotal = totalTime,
                            perguntaAtual = uiState.currentQuestionIndex + 1,
                            totalPerguntas = uiState.questions.size
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .wrapContentHeight(align = Alignment.CenterVertically),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            border = BorderStroke(2.dp, VerdeLimao)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = pergunta.pergunta,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        pergunta.opcoes.forEach { opcao ->
                            AnswerOption(
                                text = opcao,
                                isSelected = opcao == uiState.selectedAnswer,
                                isConfirmationPhase = uiState.isAnswerCorrect != null,
                                isCorrectOption = opcao == pergunta.respostaCorreta,
                                onClick = { quizViewModel.selectAnswer(opcao) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = { quizViewModel.confirmAnswer() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = uiState.selectedAnswer != null && uiState.isAnswerCorrect == null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VerdeLimao,
                                contentColor = RoxoEscuro,
                                disabledContainerColor = VerdeLimao.copy(alpha = 0.3f),
                                disabledContentColor = RoxoEscuro.copy(alpha = 0.5f)
                            )
                        ) {
                            Text("CONFIRMAR")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopBar(tempoRestanteEmSegundos: Int, tempoTotal: Int, perguntaAtual: Int, totalPerguntas: Int) {
    val minutos = tempoRestanteEmSegundos / 60
    val segundos = tempoRestanteEmSegundos % 60
    val tempoFormatado = String.format("%02d:%02d", minutos, segundos)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = tempoFormatado, color = VerdeLimao, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(text = "$perguntaAtual / $totalPerguntas", color = Color.White)
        }
        LinearProgressIndicator(
            progress = tempoRestanteEmSegundos / tempoTotal.toFloat(),
            modifier = Modifier.fillMaxWidth(),
            color = VerdeLimao
        )
    }
}

@Composable
fun AnswerOption(
    text: String,
    isSelected: Boolean,
    isConfirmationPhase: Boolean,
    isCorrectOption: Boolean,
    onClick: () -> Unit
) {
    val containerColor = when {
        !isConfirmationPhase && isSelected -> VerdeLimao.copy(alpha = 0.2f)
        isConfirmationPhase && isCorrectOption -> Color.Green.copy(alpha = 0.7f)
        isConfirmationPhase && isSelected && !isCorrectOption -> Color.Red.copy(alpha = 0.7f)
        else -> Color.Transparent
    }
    val textColor = if (isSelected && !isConfirmationPhase) VerdeLimao else Color.White
    val borderColor = if (isSelected) VerdeLimao else Color.White.copy(alpha = 0.5f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        onClick = onClick,
        enabled = !isConfirmationPhase
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = text, color = textColor, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun ExitQuizDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sair do Quiz?") },
        text = { Text("Tem certeza que deseja sair? Todo o seu progresso nesta partida será perdido.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Sim, Sair")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Não, Ficar")
            }
        }
    )
}