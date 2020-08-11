package maderski.bluetoothautoplaymusic.controls.playattempters

import android.util.Log
import kotlinx.coroutines.*

class CoroutinePlayAttempter : PlayAttempter {
    private val playAttempterScope = CoroutineScope(Dispatchers.Default) + Job()
    override fun attemptToPlay(playTasks: List<PlayTaskHolder>) {
        var playDelay: Long = START_DELAY
        val playTasksIterator = playTasks.listIterator()
        playAttempterScope.launch {
            Log.d(TAG, "Attempt to play! ${playTasksIterator.nextIndex()}  Delay: $playDelay")
            delay(playDelay)
            while(playTasksIterator.hasNext()) {
                Log.d(TAG, "Attempt to play! ${playTasksIterator.nextIndex()}  Delay: $playDelay")
                delay(playDelay)
                val playTaskHolder = playTasksIterator.next()
                playTaskHolder.playTask()
                playDelay += START_DELAY
            }
        }
    }

    override fun cancelPlayAgain() {
        playAttempterScope.cancel()
    }
    companion object {
        const val TAG = "CoroutinePlayAttempter"
        const val START_DELAY = 1000L
    }
}