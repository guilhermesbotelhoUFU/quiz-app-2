package com.example.quiz_app_2.ui.screens.quiz

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.quiz_app_2.AuthState
import com.example.quiz_app_2.AuthViewModel
import com.example.quiz_app_2.R
import com.example.quiz_app_2.navigation.QuizRoutes
import com.example.quiz_app_2.ui.theme.Quizapp2Theme
import com.example.quiz_app_2.ui.theme.RoxoEscuro
import com.example.quiz_app_2.ui.theme.VerdeLimao

@Composable
fun HomeScreen(
   navController: NavController, authViewModel: AuthViewModel, onSignOut: () -> Unit, isAdmin: Boolean = false
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        QuizSetupDialog(
            onDismiss = { showDialog = false },
            onConfirm = { tema, dificuldade ->
                showDialog = false
                navController.navigate(QuizRoutes.countdownRoute(tema, dificuldade))
            }
        )
    }

    Surface(modifier = Modifier.fillMaxSize(), color = RoxoEscuro) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo do App de Quiz",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.height(90.dp)
                )
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("JOGAR")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate(QuizRoutes.STATS) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("ESTATÍSTICAS")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate(QuizRoutes.RANKING) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("RANKING")
                }
            }

            TextButton(
                onClick = onSignOut,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Text("Sair (Logout)", color = VerdeLimao)
            }
        }
    }
}