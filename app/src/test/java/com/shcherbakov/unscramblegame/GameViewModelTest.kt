package com.shcherbakov.unscramblegame

import com.shcherbakov.unscramblegame.data.MAX_NO_OF_WORDS
import com.shcherbakov.unscramblegame.data.SCORE_INCREASE
import com.shcherbakov.unscramblegame.ui_model.GameViewModel
import org.junit.Assert.*
import org.junit.Test

class GameViewModelTest {

    private val viewModel = GameViewModel()

    private fun getUnscrambledWord(scrambledWord: String): String {

        return com.shcherbakov.unscramblegame.data.allWords.find { word ->
            word.length == scrambledWord.length &&
                    word.lowercase().toCharArray().sorted() == scrambledWord.lowercase().toCharArray().sorted()
        } ?: scrambledWord
    }

    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {

        val currentGameUiState = viewModel.uiState.value

        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()

        val updatedGameUiState = viewModel.uiState.value

        assertFalse(updatedGameUiState.isGuessedWordWrong)
        assertEquals(SCORE_INCREASE, updatedGameUiState.score)
    }

    @Test
    fun gameViewModel_IncorrectGuess_ErrorFlagSet() {

        val incorrectPlayerWord = "incorrect"

        viewModel.updateUserGuess(incorrectPlayerWord)
        viewModel.checkUserGuess()

        val currentGameUiState = viewModel.uiState.value

        assertEquals(0, currentGameUiState.score)
        assertTrue(currentGameUiState.isGuessedWordWrong)
    }


    @Test
    fun gameViewModel_Initialization_FirstWordLoaded() {

        val newViewModel = GameViewModel()
        val gameUiState = newViewModel.uiState.value


        assertEquals(1, gameUiState.currentWordCount)
        assertEquals(0, gameUiState.score)
        assertFalse(gameUiState.isGuessedWordWrong)
        assertFalse(gameUiState.isGameOver)
    }


    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly() {

        val newViewModel = GameViewModel()
        var expectedScore = 0


        repeat(MAX_NO_OF_WORDS) {
            val currentGameUiState = newViewModel.uiState.value
            val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)


            expectedScore += SCORE_INCREASE


            newViewModel.updateUserGuess(correctPlayerWord)
            newViewModel.checkUserGuess()
        }


        val finalGameUiState = newViewModel.uiState.value


        assertEquals(expectedScore, finalGameUiState.score)


        assertEquals(MAX_NO_OF_WORDS, finalGameUiState.currentWordCount)
        assertTrue(finalGameUiState.isGameOver)
    }
}