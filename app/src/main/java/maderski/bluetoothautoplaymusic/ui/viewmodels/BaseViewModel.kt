package maderski.bluetoothautoplaymusic.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import maderski.bluetoothautoplaymusic.common.AppScope
import org.koin.core.component.KoinComponent

abstract class BaseViewModel : ViewModel(), KoinComponent, CoroutineScope by AppScope()