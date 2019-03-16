package maderski.bluetoothautoplaymusic.delegatedproperties

import com.google.gson.reflect.TypeToken
import java.lang.IllegalStateException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SetOnce<T : Any>(val typeToken: TypeToken<T>) : ReadWriteProperty<Any, T> {
    private var storedObject: Any? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        if (storedObject == null) {
            throw IllegalStateException()
        } else {
            return storedObject as T
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        if (storedObject == null) {
            storedObject = value
        }
    }
}