package com.example.quiz_app_2.ui.screens.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.quiz_app_2.ui.theme.Quizapp2Theme
import com.example.quiz_app_2.ui.theme.RoxoEscuro
import com.example.quiz_app_2.ui.theme.VerdeLimao
import kotlinx.coroutines.delay

@Composable
fun CountdownScreen(
    onCountdownFinished: () -> Unit
) {
    var count by remember { mutableIntStateOf(3) }

    LaunchedEffect(key1 = Unit) {
        while (count > 0) {
            delay(1000)
            count--
        }
        onCountdownFinished()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = RoxoEscuro) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.displayLarge,
                color = VerdeLimao
            )
        }
    }
}