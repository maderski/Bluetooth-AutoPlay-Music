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

        // Set a delay and an task attempt to play again
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            playAgainTask()
            // Set Final Task to try to play
            handler?.postDelayed(finalPlayTask, ONE_SECOND_DELAY)
        }

        // Run set task after delay
        runnable?.let {
            handler?.postDelayed(it, ONE_SECOND_DELAY)
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
        const val ONE_SECOND_DELAY = 1000L
    }
}