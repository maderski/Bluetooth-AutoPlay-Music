package maderski.bluetoothautoplaymusic.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class AppScope : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Default
}