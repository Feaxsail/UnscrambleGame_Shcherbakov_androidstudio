package com.shcherbakov.unscramblegame.ui_model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.shcherbakov.unscramblegame.data.GameUiState
import com.shcherbakov.unscramblegame.data.allWords

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    lateinit var currentWord: String

    private val usedWords: MutableSet<String> = mutableSetOf()

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
}