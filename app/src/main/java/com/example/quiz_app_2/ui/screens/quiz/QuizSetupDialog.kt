package com.example.quiz_app_2.ui.screens.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.quiz_app_2.ui.theme.Quizapp2Theme
import com.example.quiz_app_2.ui.theme.RoxoEscuro
import com.example.quiz_app_2.ui.theme.VerdeLimao

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSetupDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    val temas = listOf("Música", "Cinema", "Ciência", "História", "Futebol")
    val dificuldades = listOf("Fácil", "Médio", "Difícil")
    val dificuldadeMap = mapOf("Fácil" to 1, "Médio" to 2, "Difícil" to 3)

    var temaExpandido by remember { mutableStateOf(false) }
    var temaSelecionado by remember { mutableStateOf(temas.first()) }

    val (dificuldadeSelecionada, onDificuldadeSelecionada) = remember { mutableStateOf(dificuldades.first()) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = RoxoEscuro
        ) {
            Box(contentAlignment = Alignment.Center) {
                IconButton(
                    onClick = { onDismiss() },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar diálogo",
                        tint = VerdeLimao
                    )
                }

                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Configurar Quiz",
                        style = MaterialTheme.typography.headlineMedium,
                        color = VerdeLimao
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    ExposedDropdownMenuBox(
                        expanded = temaExpandido,
                        onExpandedChange = { temaExpandido = !temaExpandido }
                    ) {
                        TextField(
                            value = temaSelecionado,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tema") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = temaExpandido)
                            },
                            modifier = Modifier.menuAnchor(),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = VerdeLimao,
                                unfocusedTextColor = VerdeLimao,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                cursorColor = VerdeLimao,
                                focusedIndicatorColor = VerdeLimao,
                                unfocusedIndicatorColor = Color.White.copy(alpha = 0.7f),
                                focusedLabelColor = VerdeLimao,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                                focusedTrailingIconColor = VerdeLimao,
                                unfocusedTrailingIconColor = VerdeLimao
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = temaExpandido,
                            onDismissRequest = { temaExpandido = false },
                            modifier = Modifier.background(RoxoEscuro)
                        ) {
                            temas.forEach { tema ->
                                DropdownMenuItem(
                                    text = { Text(tema, color = VerdeLimao) },
                                    onClick = {
                                        temaSelecionado = tema
                                        temaExpandido = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Dificuldade", color = VerdeLimao)

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        dificuldades.forEach { dificuldade ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (dificuldade == dificuldadeSelecionada),
                                        onClick = { onDificuldadeSelecionada(dificuldade) }
                                    )
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (dificuldade == dificuldadeSelecionada),
                                    onClick = { onDificuldadeSelecionada(dificuldade) },
                                    colors = RadioButtonDefaults.colors(selectedColor = VerdeLimao, unselectedColor = Color.White)
                                )
                                val numEstrelas = dificuldadeMap.getValue(dificuldade)
                                Row {
                                    for (i in 1..numEstrelas) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = VerdeLimao,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { onConfirm(temaSelecionado, dificuldadeSelecionada) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdeLimao,
                            contentColor = RoxoEscuro
                        )
                    ) {
                        Text("CONFIRMAR")
                    }
                }
            }
        }
    }
}