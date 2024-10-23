package net.clahey.golfscore.ui

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.clahey.golfscore.SetScoreRoute

class SetScoreViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var setScore: SetScoreRoute = savedStateHandle.toRoute<SetScoreRoute>()

    private val _uiState =
        MutableStateFlow(SetScoreState(if (setScore.score == null) "" else setScore.score.toString()))
    val uiState: StateFlow<SetScoreState> = _uiState.asStateFlow()

    fun setScoreText(scoreTextField: TextFieldValue) {
        _uiState.update { it.copy(scoreTextField = scoreTextField) }
    }
}

data class SetScoreState(val scoreTextField: TextFieldValue) {
    constructor(scoreText: String) : this(TextFieldValue(scoreText, TextRange(0, scoreText.length)))
}
