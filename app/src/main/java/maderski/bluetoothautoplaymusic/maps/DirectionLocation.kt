package maderski.bluetoothautoplaymusic.maps

enum class DirectionLocation(val location: String) {
    NONE("None"),
    HOME("Home"),
    WORK("Work"),
    CUSTOM("Custom");

    override fun toString(): String = location
}