package maderski.bluetoothautoplaymusic

import junit.framework.Assert.assertEquals
import maderski.bluetoothautoplaymusic.delegatedproperties.setOnceOf
import org.junit.Test

class SetOnceDelegateTest {
    private var stringProperty by setOnceOf<String>()
    private var intProperty by setOnceOf<Int>()

    @Test(expected = IllegalStateException::class)
    fun `test getting value when it hasn't been set` () {
        // Should throw an illegal state exception
        val underTest = stringProperty
    }

    @Test
    fun `test property is set only once` () {
        val expectedString = "OG string"
        stringProperty = expectedString
        var stringUnderTest = stringProperty
        stringProperty = "different string"
        stringUnderTest = stringProperty
        assertEquals(expectedString, stringUnderTest)
    }

    @Test
    fun `test setting int` () {
        val expectedInt = 1
        intProperty = expectedInt
        assertEquals(expectedInt, intProperty)
    }
}