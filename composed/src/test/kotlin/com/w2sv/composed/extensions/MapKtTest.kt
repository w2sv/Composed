package com.w2sv.composed.extensions

import org.junit.Assert.assertEquals
import org.junit.Test

class MapKtTest {

    @Test
    fun toMutableStateMap() {
        val map = mapOf(4 to 8, 69 to 43, 9 to 87967)
        val converted = map.toMutableStateMap()
        assertEquals(map, converted)
    }
}