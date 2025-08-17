package com.example.quiz_app_2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quiz_app_2.AuthState
import com.example.quiz_app_2.AuthViewModel
import com.example.quiz_app_2.ui.screens.quiz.CountdownScreen
import com.example.quiz_app_2.ui.screens.quiz.HomeScreen
import com.example.quiz_app_2.ui.screens.quiz.QuizScreen
import com.example.quiz_app_2.ui.screens.quiz.QuizViewModel
import com.example.quiz_app_2.ui.screens.stats.StatsScreen
import com.example.quiz_app_2.ui.screens.ranking.RankingScreen

object QuizRoutes {
    const val HOME = "quiz_home"
    const val STATS = "quiz_stats"
    const val COUNTDOWN = "quiz_countdown/{tema}/{dificuldade}"
    const val GAME = "quiz_game/{tema}/{dificuldade}"
    const val RANKING = "quiz_ranking"

    fun countdownRoute(tema: String, dificuldade: String) = "quiz_countdown/$tema/$dificuldade"
    fun gameRoute(tema: String, dificuldade: String) = "quiz_game/$tema/$dificuldade"
}

@Composable
fun QuizNavHost(
    authViewModel: AuthViewModel,
    onNavigateToAuth: () -> Unit
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.observeAsState()
    val userProfile = if (authState is AuthState.Authenticated) {
        (authState as AuthState.Authenticated).userProfile
    } else null

    NavHost(navController = navController, startDestination = QuizRoutes.HOME) {
        composable(QuizRoutes.HOME) {
            HomeScreen(
                navController = navController,
                authViewModel = authViewModel,
                onSignOut = onNavigateToAuth,
                isAdmin = userProfile?.isAdmin ?: false
            )
        }
        composable(QuizRoutes.STATS) {
            StatsScreen(navController = navController)
        }
        composable(
            route = QuizRoutes.COUNTDOWN,
            arguments = listOf(
                navArgument("tema") { type = NavType.StringType },
                navArgument("dificuldade") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tema = backStackEntry.arguments?.getString("tema") ?: ""
            val dificuldade = backStackEntry.arguments?.getString("dificuldade") ?: ""
            CountdownScreen(
                onCountdownFinished = {
                    navController.navigate(QuizRoutes.gameRoute(tema, dificuldade)) {
                        popUpTo(QuizRoutes.COUNTDOWN) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = QuizRoutes.GAME,
            arguments = listOf(
                navArgument("tema") { type = NavType.StringType },
                navArgument("dificuldade") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tema = backStackEntry.arguments?.getString("tema") ?: ""
            val dificuldade = backStackEntry.arguments?.getString("dificuldade") ?: ""
            val quizViewModel: QuizViewModel = hiltViewModel()
            QuizScreen(
                navController = navController,
                tema = tema,
                dificuldade = dificuldade,
                quizViewModel = quizViewModel
            )
        }
        composable(QuizRoutes.RANKING) {
            RankingScreen(navController = navController)
        }
    }
}