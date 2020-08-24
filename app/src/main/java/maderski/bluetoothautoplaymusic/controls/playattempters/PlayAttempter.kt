package maderski.bluetoothautoplaymusic.controls.playattempters

interface PlayAttempter {
    fun attemptToPlay(playTasks: List<PlayTaskHolder>)

    fun cancelPlayAgain()
}