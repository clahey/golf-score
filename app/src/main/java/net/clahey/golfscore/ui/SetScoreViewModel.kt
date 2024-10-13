package net.clahey.golfscore.ui

import android.app.Application
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.clahey.golfscore.SetScore
import net.clahey.golfscore.data.database.AppDatabase

class SetScoreViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {
    var setScore: SetScore = savedStateHandle.toRoute<SetScore>()

    private val db = AppDatabase.getInstance(application.applicationContext)
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
