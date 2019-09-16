package maderski.bluetoothautoplaymusic.controls.mediaplayer

import android.os.Handler
import android.os.Looper

class PlayAttempter {
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    fun attemptToPlay(
            playTask: () -> Unit,
            playAgainTask: () -> Unit,
            finalPlayTask: () -> Unit
    ) {
        // Attempt to play
        playTask()

        // Set a delay and attempt to play again
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            playAgainTask()
            handler?.postDelayed(finalPlayTask, DELAY)
        }
        runnable?.let {
            handler?.postDelayed(it, DELAY)
        }
    }

    fun cancelPlayAgain() {
        if (handler != null && runnable != null) {
            runnable?.let {
                handler?.removeCallbacks(it)
            }

            handler = null
            runnable = null
        }
    }

    companion object {
        const val DELAY = 1000L
    }
}