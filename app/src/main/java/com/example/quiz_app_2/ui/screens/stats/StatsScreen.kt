package com.example.quiz_app_2.ui.screens.stats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.quiz_app_2.R
import com.example.quiz_app_2.data.model.*
import com.example.quiz_app_2.ui.theme.Quizapp2Theme
import com.example.quiz_app_2.ui.theme.RoxoEscuro
import com.example.quiz_app_2.ui.theme.VerdeLimao
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatsScreen(
    navController: NavController,
    statsViewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by statsViewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize(), color = RoxoEscuro) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = VerdeLimao
                    )
                }
                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.userProfile != null -> {
                    val userProfile = uiState.userProfile!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item { Header(email = userProfile.email) }
                        item {
                            SectionTitle("Resumo Geral")
                            OverallStats(stats = userProfile.stats)
                        }
                        item { SectionTitle("Por Tema") }
                        items(userProfile.stats.statsByTheme.entries.toList()) { (tema, stat) ->
                            StatItem(tema = tema, estatistica = stat)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        item { SectionTitle("Últimos Jogos") }
                        items(userProfile.stats.recentGames) { game ->
                            GameResultItem(result = game)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        item { Spacer(modifier = Modifier.height(32.dp)) }
                    }
                }
            }

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = VerdeLimao)
            }
        }
    }
}

@Composable
fun Header(email: String) {
    Column(
        modifier = Modifier.padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painterResource(R.drawable.estatistica), "Ícone de estatísticas", modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(email, style = MaterialTheme.typography.titleMedium, color = Color.White)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        color = VerdeLimao,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    )
}

@Composable
fun OverallStats(stats: UserStats) {
    val precisaoGeral = if (stats.totalQuestionsAnswered > 0) {
        stats.totalCorrectAnswers.toFloat() / stats.totalQuestionsAnswered
    } else 0f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            label = "Jogos Completados",
            value = stats.totalGamesPlayed.toString(),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Precisão Geral",
            value = "${(precisaoGeral * 100).toInt()}%",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = RoxoEscuro),
        border = BorderStroke(1.dp, VerdeLimao.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun StatItem(tema: String, estatistica: ThemeStat) {
    val percentual = if (estatistica.totalQuestionsAnswered > 0) {
        estatistica.correctAnswers.toFloat() / estatistica.totalQuestionsAnswered
    } else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = RoxoEscuro.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, VerdeLimao.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tema,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = VerdeLimao
                )
                Text(
                    text = "${estatistica.gamesPlayed} jogos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { percentual },
                    modifier = Modifier.size(60.dp),
                    color = VerdeLimao,
                    strokeWidth = 6.dp,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
                Text(
                    text = "${(percentual * 100).toInt()}%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun GameResultItem(result: GameResult) {
    val dateFormat = SimpleDateFormat("dd/MM/yy 'às' HH:mm", Locale.getDefault())
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = RoxoEscuro.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(result.theme, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text(dateFormat.format(Date(result.timestamp)), style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
            }
            Text("${result.score} pts", style = MaterialTheme.typography.titleLarge, color = VerdeLimao, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview
@Composable
fun StatsScreenPreview() {
    Quizapp2Theme {
        StatsScreen(navController = rememberNavController())
    }
}