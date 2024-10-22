package net.clahey.golfscore.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavController

@Composable
fun <T> DialogParent(
    receiver: DialogResponseReceiver<T>,
    observer: (T) -> Unit,
) {
    DisposableEffect(true) {
        receiver.addScoreObserver(observer)
        onDispose {
            receiver.removeScoreObserver(observer)
        }
    }
}

class DialogResponseReceiver<T>(val navController: NavController, val key: String) {
    fun addScoreObserver(observer: (T) -> Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<T>(
            key
        )?.observeForever(observer)
    }

    fun removeScoreObserver(observer: (T) -> Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<T>(
            key
        )?.removeObserver(observer)
    }
}

fun <T> sendDialogResponse(navController: NavController, key: String, response: T) {
    navController.previousBackStackEntry?.savedStateHandle?.set(key, response)
}