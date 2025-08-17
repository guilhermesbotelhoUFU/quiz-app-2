package com.example.quiz_app_2

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quiz_app_2.navigation.QuizNavHost
import com.example.quiz_app_2.ui.screens.auth.LoginScreen
import com.example.quiz_app_2.ui.screens.auth.RegisterScreen
import com.example.quiz_app_2.ui.screens.quiz.HomeScreen

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder ={
        composable("login"){
            LoginScreen(modifier, navController, authViewModel)
        }
        composable("signup"){
            RegisterScreen(modifier, navController, authViewModel)
        }
        composable("home") {
            QuizNavHost(
                authViewModel = authViewModel,
                onNavigateToAuth = {
                    authViewModel.signout()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    })
}