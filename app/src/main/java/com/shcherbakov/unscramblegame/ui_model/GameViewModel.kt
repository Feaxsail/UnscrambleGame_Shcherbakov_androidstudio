package com.shcherbakov.unscramblegame.ui_model
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.shcherbakov.unscramblegame.data.GameUiState
import com.shcherbakov.unscramblegame.data.allWords
import com.shcherbakov.unscramblegame.data.MAX_NO_OF_WORDS
import com.shcherbakov.unscramblegame.data.SCORE_INCREASE

class GameViewModel : ViewModel() {


    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()


    lateinit var currentWord: String


    private val usedWords: MutableSet<String> = mutableSetOf()


    var userGuess: String by mutableStateOf("")
        private set

    init {
        resetGame()
    }


    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(
            currentScrambledWord = pickRandomWordAndShuffle()
        )
    }


    private fun pickRandomWordAndShuffle(): String {

        var word = allWords.random()

        while (word in usedWords) {
            word = allWords.random()
        }

        usedWords.add(word)

        currentWord = word

        return shuffleCurrentWord(word)
    }


    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        do {
            tempWord.shuffle()
        } while (String(tempWord).equals(word, ignoreCase = true))
        return String(tempWord)
    }


    fun updateUserGuess(guess: String) {
        userGuess = guess
    }


    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size >= MAX_NO_OF_WORDS) {

            _uiState.value = GameUiState(
                isGuessedWordWrong = false,
                score = updatedScore,
                isGameOver = true
            )
        } else {

            val updatedScrambledWord = pickRandomWordAndShuffle()
            _uiState.value = GameUiState(
                currentScrambledWord = updatedScrambledWord,
                currentWordCount = _uiState.value.currentWordCount.inc(),
                score = updatedScore
            )
        }
    }


    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {

            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        } else {

            _uiState.value = _uiState.value.copy(
                isGuessedWordWrong = true
            )
        }

        updateUserGuess("")
    }


    fun skipWord() {
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }
}