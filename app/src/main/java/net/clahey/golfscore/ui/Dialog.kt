package net.clahey.golfscore.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavHostController

@Composable
fun <T> DialogParent(
    dialog: DialogWithResponse<T>,
    observer: (T) -> Unit,
) {
    DisposableEffect(true) {
        dialog.addResponseObserver(observer)
        onDispose {
            dialog.removeResponseObserver(observer)
        }
    }
}

data class DialogWithResponse<T>(val key: String, val navController: NavHostController) {
    fun sendResponse(response: T) {
        navController.previousBackStackEntry?.savedStateHandle?.set(key, response)
    }

    fun addResponseObserver(observer: (T) -> Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<T>(
            key
        )?.observeForever(observer)
    }

    fun removeResponseObserver(observer: (T) -> Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<T>(
            key
        )?.removeObserver(observer)
    }
}
