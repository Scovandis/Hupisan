package my.notes.HHHH

import android.text.Spanned
import my.notes.HHHH.util.InputFilterMinMax
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InputFilterMinMaxTest {

    private class MockSpanned(private val text: String) : Spanned, CharSequence by text {
        @Suppress("UNCHECKED_CAST")
        override fun <T : Any?> getSpans(start: Int, end: Int, type: Class<T>?): Array<T> {
            return java.lang.reflect.Array.newInstance(type ?: Any::class.java, 0) as Array<T>
        }
        override fun getSpanStart(tag: Any?): Int = -1
        override fun getSpanEnd(tag: Any?): Int = -1
        override fun getSpanFlags(tag: Any?): Int = 0
        override fun nextSpanTransition(start: Int, limit: Int, type: Class<*>?): Int = limit
        override fun toString(): String = text
    }

    @Test
    fun filter_validRange_returnsNull() {
        val filter = InputFilterMinMax(1, 100)
        val dest = MockSpanned("5")
        val result = filter.filter("0", 0, 1, dest, 1, 1)
        // In range 50 -> should accept by returning null
        assertNull(result)
    }

    @Test
    fun filter_outOfRange_returnsEmptyString() {
        val filter = InputFilterMinMax(1, 100)
        val dest = MockSpanned("50")
        val result = filter.filter("0", 0, 1, dest, 2, 2)
        // 500 is > 100 -> should reject by returning ""
        assertEquals("", result)
    }
}
