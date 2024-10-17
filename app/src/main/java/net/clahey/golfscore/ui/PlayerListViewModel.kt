package net.clahey.golfscore.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import net.clahey.golfscore.data.database.AppDatabase
import net.clahey.golfscore.data.database.Player

class PlayerListViewModel (application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application.applicationContext)
    val players: Flow<List<Player>> = db.playerDao().getAllFlow()
}
