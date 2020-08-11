package maderski.bluetoothautoplaymusic.controls.playattempters

import android.os.Handler
import android.os.Looper
import android.util.Log

class BasicPlayAttempter : PlayAttempter {
    private var handler: Handler? = null
    private var playTasksIteratively: Runnable? = null

    override fun attemptToPlay(playTasks: List<PlayTaskHolder>) {
        var playDelay: Long = START_DELAY
        val playTasksIterator = playTasks.listIterator()
        handler = Handler(Looper.getMainLooper())
        playTasksIteratively = Runnable {
            if (playTasksIterator.hasNext()) {
                val playTaskHolder = playTasksIterator.next()
                handler?.postDelayed({
                    Log.d(TAG, "Attempt to play! ${playTasksIterator.nextIndex()}  Delay: $playDelay")
                    handler?.post(playTaskHolder.playTask)
                    playTasksIteratively?.let {
                        playDelay += START_DELAY
                        handler?.postDelayed(it, playDelay)
                    }
                }, START_DELAY)
            } else {
                Log.d(TAG, "CANCEL ATTEMPT TO PLAY")
                cancelPlayAgain()
            }
        }

        playTasksIteratively?.let {
            handler?.post(it)
        }
    }

    override fun cancelPlayAgain() {
        playTasksIteratively?.let {
            handler?.removeCallbacks(it)
        }

        handler = null
        playTasksIteratively = null
    }

    companion object {
        const val TAG = "BasicPlayAttempter"
        const val START_DELAY = 1000L
    }
}