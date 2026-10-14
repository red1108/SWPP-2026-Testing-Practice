package com.example.swpp.tododapp.tasks

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class DueDateUtilsTest {
    @Test
    fun dueDateLabel_futureWeek_returnsDMinus7() {
        // GIVEN - Exact calendar dates from the reviewed spec
        val today = LocalDate.parse("2026-10-14")
        val dueDates = listOf<LocalDate?>(LocalDate.parse("2026-10-21"))
        // WHEN - Build each label
        val actual = dueDates.map { dueDateLabel(it, today, false) }
        // THEN - Compare with literal calendar-day expectations
        assertEquals(listOf("D-7"), actual)
    }

    @Test
    fun dueDateLabel_today_returnsDDay() {
        // GIVEN - Exact calendar dates from the reviewed spec
        val today = LocalDate.parse("2026-10-14")
        val dueDates = listOf<LocalDate?>(LocalDate.parse("2026-10-14"))
        // WHEN - Build each label
        val actual = dueDates.map { dueDateLabel(it, today, false) }
        // THEN - Compare with literal calendar-day expectations
        assertEquals(listOf("D-Day"), actual)
    }

    @Test
    fun dueDateLabel_overdueWeek_returnsDPlus7() {
        // GIVEN - Exact calendar dates from the reviewed spec
        val today = LocalDate.parse("2026-10-14")
        val dueDates = listOf<LocalDate?>(LocalDate.parse("2026-10-07"))
        // WHEN - Build each label
        val actual = dueDates.map { dueDateLabel(it, today, false) }
        // THEN - Compare with literal calendar-day expectations
        assertEquals(listOf("D+7"), actual)
    }

    @Test
    fun dueDateLabel_activeWithoutDate_returnsEmpty() {
        // GIVEN - Exact calendar dates from the reviewed spec
        val today = LocalDate.parse("2026-10-14")
        val dueDates = listOf<LocalDate?>(null)
        // WHEN - Build each label
        val actual = dueDates.map { dueDateLabel(it, today, false) }
        // THEN - Compare with literal calendar-day expectations
        assertEquals(listOf(""), actual)
    }

    @Test
    fun dueDateLabel_completedWithDate_returnsClear() {
        // GIVEN - Exact calendar dates from the reviewed spec
        val today = LocalDate.parse("2026-10-14")
        val dueDates = listOf<LocalDate?>(LocalDate.parse("2026-10-21"))
        // WHEN - Build each label
        val actual = dueDates.map { dueDateLabel(it, today, true) }
        // THEN - Compare with literal calendar-day expectations
        assertEquals(listOf("CLEAR"), actual)
    }

    @Test
    fun dueDateLabel_completedWithoutDate_returnsClear() {
        // GIVEN - Exact calendar dates from the reviewed spec
        val today = LocalDate.parse("2026-10-14")
        val dueDates = listOf<LocalDate?>(null)
        // WHEN - Build each label
        val actual = dueDates.map { dueDateLabel(it, today, true) }
        // THEN - Compare with literal calendar-day expectations
        assertEquals(listOf("CLEAR"), actual)
    }

    @Test
    fun dueDateLabel_nullableInput_returnsEmptyWithoutThrowing() {
        // GIVEN - Exact calendar dates from the reviewed spec
        val today = LocalDate.parse("2026-10-14")
        val dueDates = listOf<LocalDate?>(null)
        // WHEN - Build each label
        val actual = dueDates.map { dueDateLabel(it, today, false) }
        // THEN - Compare with literal calendar-day expectations
        assertEquals(listOf(""), actual)
    }

    @Test
    fun dueDateLabel_yesterdayTodayTomorrow_returnsBoundaryLabels() {
        // GIVEN - Exact calendar dates from the reviewed spec
        val today = LocalDate.parse("2026-10-14")
        val dueDates = listOf<LocalDate?>(LocalDate.parse("2026-10-13"), LocalDate.parse("2026-10-14"), LocalDate.parse("2026-10-15"))
        // WHEN - Build each label
        val actual = dueDates.map { dueDateLabel(it, today, false) }
        // THEN - Compare with literal calendar-day expectations
        assertEquals(listOf("D+1", "D-Day", "D-1"), actual)
    }

    @Test
    fun dueDateLabel_monthEnd_returnsCalendarDayLabels() {
        // GIVEN - Exact calendar dates from the reviewed spec
        val today = LocalDate.parse("2026-01-31")
        val dueDates = listOf<LocalDate?>(LocalDate.parse("2026-01-31"), LocalDate.parse("2026-02-01"), LocalDate.parse("2026-02-02"))
        // WHEN - Build each label
        val actual = dueDates.map { dueDateLabel(it, today, false) }
        // THEN - Compare with literal calendar-day expectations
        assertEquals(listOf("D-Day", "D-1", "D-2"), actual)
    }

    @Test
    fun dueDateLabel_fullMonthAndAdjacentDates_returnsTotalDayLabels() {
        // GIVEN - Exact calendar dates from the reviewed spec
        val today = LocalDate.parse("2026-01-15")
        val dueDates = listOf<LocalDate?>(LocalDate.parse("2026-02-14"), LocalDate.parse("2026-02-15"), LocalDate.parse("2026-02-16"))
        // WHEN - Build each label
        val actual = dueDates.map { dueDateLabel(it, today, false) }
        // THEN - Compare with literal calendar-day expectations
        assertEquals(listOf("D-30", "D-31", "D-32"), actual)
    }

    @Test
    fun dueDateLabel_yearEnd_returnsCalendarDayLabels() {
        // GIVEN - Exact calendar dates from the reviewed spec
        val today = LocalDate.parse("2026-12-31")
        val dueDates = listOf<LocalDate?>(LocalDate.parse("2026-12-31"), LocalDate.parse("2027-01-01"), LocalDate.parse("2027-01-02"))
        // WHEN - Build each label
        val actual = dueDates.map { dueDateLabel(it, today, false) }
        // THEN - Compare with literal calendar-day expectations
        assertEquals(listOf("D-Day", "D-1", "D-2"), actual)
    }

    @Test
    fun dueDateLabel_leapDay_returnsCalendarDayLabels() {
        // GIVEN - Exact calendar dates from the reviewed spec
        val today = LocalDate.parse("2028-02-28")
        val dueDates = listOf<LocalDate?>(LocalDate.parse("2028-02-28"), LocalDate.parse("2028-02-29"), LocalDate.parse("2028-03-01"))
        // WHEN - Build each label
        val actual = dueDates.map { dueDateLabel(it, today, false) }
        // THEN - Compare with literal calendar-day expectations
        assertEquals(listOf("D-Day", "D-1", "D-2"), actual)
    }

    @Test
    fun dueDateLabel_undatedCompletionSequence_returnsEmptyClearEmpty() {
        // GIVEN - An undated task and explicit reference date
        val today = LocalDate.parse("2026-10-14")
        val completionStates = listOf(false, true, false)
        // WHEN - Evaluate each completion state
        val actual = completionStates.map { dueDateLabel(null, today, it) }
        // THEN - Completion overrides absence only while completed
        assertEquals(listOf("", "CLEAR", ""), actual)
    }

    @Test
    fun dueDateLabel_completedPastTodayFuture_returnsClear() {
        // GIVEN - Exact calendar dates from the reviewed spec
        val today = LocalDate.parse("2026-10-14")
        val dueDates = listOf<LocalDate?>(LocalDate.parse("2026-10-13"), LocalDate.parse("2026-10-14"), LocalDate.parse("2026-10-15"))
        // WHEN - Build each label
        val actual = dueDates.map { dueDateLabel(it, today, true) }
        // THEN - Compare with literal calendar-day expectations
        assertEquals(listOf("CLEAR", "CLEAR", "CLEAR"), actual)
    }

    @Test
    fun dueDateLabel_overdueMonthAndAdjacentDates_returnsTotalDayLabels() {
        // GIVEN - Exact calendar dates from the reviewed spec
        val today = LocalDate.parse("2026-02-15")
        val dueDates = listOf<LocalDate?>(LocalDate.parse("2026-01-14"), LocalDate.parse("2026-01-15"), LocalDate.parse("2026-01-16"))
        // WHEN - Build each label
        val actual = dueDates.map { dueDateLabel(it, today, false) }
        // THEN - Compare with literal calendar-day expectations
        assertEquals(listOf("D+32", "D+31", "D+30"), actual)
    }

    @Test
    fun dueDateLabel_futureYearAndAdjacentDates_returnsTotalDayLabels() {
        // GIVEN - Exact calendar dates from the reviewed spec
        val today = LocalDate.parse("2026-12-15")
        val dueDates = listOf<LocalDate?>(LocalDate.parse("2027-12-14"), LocalDate.parse("2027-12-15"), LocalDate.parse("2027-12-16"))
        // WHEN - Build each label
        val actual = dueDates.map { dueDateLabel(it, today, false) }
        // THEN - Compare with literal calendar-day expectations
        assertEquals(listOf("D-364", "D-365", "D-366"), actual)
    }

    @Test
    fun dueDateLabel_overdueYearAndAdjacentDates_returnsTotalDayLabels() {
        // GIVEN - Exact calendar dates from the reviewed spec
        val today = LocalDate.parse("2027-12-15")
        val dueDates = listOf<LocalDate?>(LocalDate.parse("2026-12-14"), LocalDate.parse("2026-12-15"), LocalDate.parse("2026-12-16"))
        // WHEN - Build each label
        val actual = dueDates.map { dueDateLabel(it, today, false) }
        // THEN - Compare with literal calendar-day expectations
        assertEquals(listOf("D+366", "D+365", "D+364"), actual)
    }

    @Test
    fun dueDateLabel_overdueLeapDay_returnsCalendarDayLabels() {
        // GIVEN - Exact calendar dates from the reviewed spec
        val today = LocalDate.parse("2028-03-01")
        val dueDates = listOf<LocalDate?>(LocalDate.parse("2028-02-28"), LocalDate.parse("2028-02-29"), LocalDate.parse("2028-03-01"))
        // WHEN - Build each label
        val actual = dueDates.map { dueDateLabel(it, today, false) }
        // THEN - Compare with literal calendar-day expectations
        assertEquals(listOf("D+2", "D+1", "D-Day"), actual)
    }

    @Test
    fun dueDateLabel_nonLeapFebruary_returnsCalendarDayLabels() {
        // GIVEN - Exact calendar dates from the reviewed spec
        val today = LocalDate.parse("2027-02-28")
        val dueDates = listOf<LocalDate?>(LocalDate.parse("2027-02-28"), LocalDate.parse("2027-03-01"), LocalDate.parse("2027-03-02"))
        // WHEN - Build each label
        val actual = dueDates.map { dueDateLabel(it, today, false) }
        // THEN - Compare with literal calendar-day expectations
        assertEquals(listOf("D-Day", "D-1", "D-2"), actual)
    }
}
