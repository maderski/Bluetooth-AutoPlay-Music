package maderski.bluetoothautoplaymusic.helpers

import com.google.gson.Gson
import maderski.bluetoothautoplaymusic.bluetooth.models.BAPMDevice
import maderski.bluetoothautoplaymusic.wrappers.BTDeviceSetWrapper

class SerializationHelper(private val gson: Gson) {
    fun serializeBTDeviceSet(bapmDeviceSet: Set<BAPMDevice>): String =
            gson.toJson(BTDeviceSetWrapper(bapmDeviceSet))
    fun deserializeBTDeviceSet(serializedBTDeviceSet: String): Set<BAPMDevice> {
        val wrappedSet = gson.fromJson(serializedBTDeviceSet, BTDeviceSetWrapper::class.java)
        return wrappedSet.bapmDeviceSet
    }
}