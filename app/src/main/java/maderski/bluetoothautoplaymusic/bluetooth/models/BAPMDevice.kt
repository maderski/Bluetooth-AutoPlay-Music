package maderski.bluetoothautoplaymusic.bluetooth.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BAPMDevice(
        val name: String,
        val macAddress: String
) : Parcelable