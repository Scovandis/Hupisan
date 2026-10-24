package my.notes.HHHH

import android.text.SpannableStringBuilder
import my.notes.HHHH.util.InputFilterMinMax
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InputFilterMinMaxTest {

    @Test
    fun filter_validRange_returnsNull() {
        val filter = InputFilterMinMax(1, 100)
        val dest = SpannableStringBuilder("5")
        val result = filter.filter("0", 0, 1, dest, 1, 1)
        // In range 50 -> should accept by returning null
        assertNull(result)
    }

    @Test
    fun filter_outOfRange_returnsEmptyString() {
        val filter = InputFilterMinMax(1, 100)
        val dest = SpannableStringBuilder("50")
        val result = filter.filter("0", 0, 1, dest, 2, 2)
        // 500 is > 100 -> should reject by returning ""
        assertEquals("", result)
    }
}
