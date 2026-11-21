package com.hupisan

import com.hupisan.notification.ReminderScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar

class ReminderSchedulerTest {

    @Test
    fun parseReminderTimeMillis_preservesUserSelectedHourAndMinute() {
        val millis = ReminderScheduler.parseReminderTimeMillis("25/12/2026", "20:45")
        assertNotNull(millis)
        val cal = Calendar.getInstance().apply { timeInMillis = millis!! }
        assertEquals(2026, cal.get(Calendar.YEAR))
        assertEquals(Calendar.DECEMBER, cal.get(Calendar.MONTH))
        assertEquals(25, cal.get(Calendar.DAY_OF_MONTH))
        assertEquals(20, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(45, cal.get(Calendar.MINUTE))
    }

    @Test
    fun parseReminderTimeMillis_defaultsTo0900WhenTimeBlankOrDash() {
        val millisDash = ReminderScheduler.parseReminderTimeMillis("25/12/2026", "-")
        val millisBlank = ReminderScheduler.parseReminderTimeMillis("25/12/2026", "")
        assertNotNull(millisDash)
        assertEquals(millisDash, millisBlank)
        val cal = Calendar.getInstance().apply { timeInMillis = millisDash!! }
        assertEquals(9, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, cal.get(Calendar.MINUTE))
    }

    @Test
    fun parseReminderTimeMillis_returnsNullForInvalidOrBlankDate() {
        assertNull(ReminderScheduler.parseReminderTimeMillis("", "09:00"))
        assertNull(ReminderScheduler.parseReminderTimeMillis(null, "09:00"))
        assertNull(ReminderScheduler.parseReminderTimeMillis("99/99/2026", "09:00"))
    }
}
