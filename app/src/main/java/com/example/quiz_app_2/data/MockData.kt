package com.example.quiz_app_2.data

import com.example.quiz_app_2.data.model.Question

object MockData {
    fun getMockQuestions(): List<Question> {
        return listOf(
            Question(
                id = "1",
                pergunta = "Qual é a organela celular responsável pela respiração e produção de energia (ATP)?",
                opcoes = listOf("Núcleo", "Ribossomo", "Mitocôndria", "Lisossomo"),
                respostaCorreta = "Mitocôndria",
                tema = "Ciência",
                dificuldade = "Médio"
            ),
            Question(
                id = "2",
                pergunta = "Qual é o símbolo químico para o elemento Ouro?",
                opcoes = listOf("Ag", "Fe", "Au", "Pb"),
                respostaCorreta = "Au",
                tema = "Ciência",
                dificuldade = "Médio"
            ),
            Question(
                id = "3",
                pergunta = "Que força fundamental mantém os planetas em órbita ao redor do Sol?",
                opcoes = listOf("Eletromagnetismo", "Força Nuclear Fraca", "Força Nuclear Forte", "Gravidade"),
                respostaCorreta = "Gravidade",
                tema = "Ciência",
                dificuldade = "Médio"
            ),
            Question(
                id = "4",
                pergunta = "Qual gás as plantas absorvem da atmosfera durante a fotossíntese?",
                opcoes = listOf("Oxigênio", "Dióxido de Carbono", "Nitrogênio", "Hidrogênio"),
                respostaCorreta = "Dióxido de Carbono",
                tema = "Ciência",
                dificuldade = "Médio"
            ),
            Question(
                id = "5",
                pergunta = "Qual é o maior órgão do corpo humano?",
                opcoes = listOf("Cérebro", "Fígado", "Pele", "Intestino"),
                respostaCorreta = "Pele",
                tema = "Ciência",
                dificuldade = "Médio"
            ),
            Question(
                id = "6",
                pergunta = "Quantos planetas existem em nosso Sistema Solar?",
                opcoes = listOf("7", "8", "9", "10"),
                respostaCorreta = "8",
                tema = "Ciência",
                dificuldade = "Médio"
            ),
            Question(
                id = "7",
                pergunta = "Qual é a substância natural mais dura encontrada na Terra?",
                opcoes = listOf("Quartzo", "Ouro", "Ferro", "Diamante"),
                respostaCorreta = "Diamante",
                tema = "Ciência",
                dificuldade = "Médio"
            ),
            Question(
                id = "8",
                pergunta = "A velocidade da luz no vácuo é de aproximadamente...",
                opcoes = listOf("300 km/s", "3.000 km/s", "30.000 km/s", "300.000 km/s"),
                respostaCorreta = "300.000 km/s",
                tema = "Ciência",
                dificuldade = "Médio"
            ),
            Question(
                id = "9",
                pergunta = "O processo de conversão de um líquido em gás é chamado de:",
                opcoes = listOf("Condensação", "Solidificação", "Evaporação", "Sublimação"),
                respostaCorreta = "Evaporação",
                tema = "Ciência",
                dificuldade = "Médio"
            ),
            Question(
                id = "10",
                pergunta = "Que tipo de estrela é o nosso Sol?",
                opcoes = listOf("Gigante Vermelha", "Anã Branca", "Anã Amarela", "Supernova"),
                respostaCorreta = "Anã Amarela",
                tema = "Ciência",
                dificuldade = "Médio"
            )
        )
    }
}