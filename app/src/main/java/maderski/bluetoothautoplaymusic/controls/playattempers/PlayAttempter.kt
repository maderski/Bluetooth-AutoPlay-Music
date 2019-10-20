package maderski.bluetoothautoplaymusic.controls.playattempers

interface PlayAttempter {
    fun attemptToPlay(playTasks: List<PlayTaskHolder>)

    fun cancelPlayAgain()
}