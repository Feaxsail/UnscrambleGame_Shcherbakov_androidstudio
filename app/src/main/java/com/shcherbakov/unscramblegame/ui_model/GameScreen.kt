package com.shcherbakov.unscramblegame.ui_model

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.shcherbakov.unscramblegame.data.MAX_NO_OF_WORDS
import com.shcherbakov.unscramblegame.data.SCORE_INCREASE

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = viewModel()
) {
    val gameUiState by gameViewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Unscramble Game",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 32.sp
        )

        GameStatus(
            wordCount = gameUiState.currentWordCount,
            score = gameUiState.score
        )

        GameLayout(
            currentScrambledWord = gameUiState.currentScrambledWord,
            userGuess = gameViewModel.userGuess,
            onUserGuessChanged = gameViewModel::updateUserGuess,
            onKeyboardDone = gameViewModel::checkUserGuess,
            isGuessWrong = gameUiState.isGuessedWordWrong,
            onSkipWord = gameViewModel::skipWord
        )
    }


    if (gameUiState.isGameOver) {
        GameOverDialog(
            score = gameUiState.score,
            wordCount = gameUiState.currentWordCount,
            onPlayAgain = gameViewModel::resetGame
        )
    }
}

@Composable
fun GameStatus(
    wordCount: Int,
    score: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Слово $wordCount из $MAX_NO_OF_WORDS",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Счёт: $score",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun GameLayout(
    currentScrambledWord: String,
    userGuess: String,
    onUserGuessChanged: (String) -> Unit,
    onKeyboardDone: () -> Unit,
    isGuessWrong: Boolean,
    onSkipWord: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(158.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box(
                modifier = modifier.fillMaxSize()
            ) {
                Text(
                    text = currentScrambledWord,
                    style = MaterialTheme.typography.displayLarge,
                    modifier = modifier.align(Alignment.Center)
                )
            }
        }

        OutlinedTextField(
            value = userGuess,
            onValueChange = onUserGuessChanged,
            label = { Text("Введите слово") },
            modifier = modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onKeyboardDone() }
            ),
            isError = isGuessWrong
        )

        if (isGuessWrong) {
            Text(
                text = "Неправильный ответ! Попробуйте ещё раз.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = { onKeyboardDone() },
            modifier = modifier.fillMaxWidth()
        ) {
            Text("Проверить")
        }

        OutlinedButton(
            onClick = { onSkipWord() },
            modifier = modifier.fillMaxWidth()
        ) {
            Text("Пропустить")
        }
    }
}

@Composable
fun GameOverDialog(
    score: Int,
    wordCount: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = { /* Do nothing */ },
        title = { Text("Игра окончена!") },
        text = {
            Column {
                Text("Поздравляем! Вы завершили игру.")
                Text("Ваш счёт: $score из ${wordCount * SCORE_INCREASE}")
            }
        },
        confirmButton = {
            TextButton(onClick = onPlayAgain) {
                Text("Играть снова")
            }
        },
        modifier = modifier
    )
}