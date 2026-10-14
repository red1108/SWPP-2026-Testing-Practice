package com.example.swpp.tododapp.tasks

import android.view.View
import android.widget.TextView
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.swpp.tododapp.data.Task
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class TasksListBindingsTest {
    private fun task(dueDate: LocalDate?, completed: Boolean = false) =
        Task("Task", "Description", completed, "task-1", dueDate)

    private fun newView(text: String, visibility: Int) =
        TextView(InstrumentationRegistry.getInstrumentation().targetContext).apply {
            this.text = text
            this.visibility = visibility
        }

    private fun bindStates(view: TextView, tasks: List<Task?>): List<Pair<String, Int>> =
        tasks.map {
            setDueDateLabel(view, it)
            view.text.toString() to view.visibility
        }

    @Test
    @UiThreadTest
    fun setDueDateLabel_activeFuture_showsDMinus7() {
        // GIVEN - One real TextView and the reviewed task fixture
        val today = LocalDate.now()
        val view = newView("", View.GONE)
        val tasks = listOf(task(today.plusDays(7)))
        // WHEN - Rebind the same view and record every resulting state
        val actual = bindStates(view, tasks)
        // THEN - Check literal text and visibility after every call
        assertEquals(listOf("D-7" to View.VISIBLE), actual)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_activeToday_showsDDay() {
        // GIVEN - One real TextView and the reviewed task fixture
        val today = LocalDate.now()
        val view = newView("", View.GONE)
        val tasks = listOf(task(today))
        // WHEN - Rebind the same view and record every resulting state
        val actual = bindStates(view, tasks)
        // THEN - Check literal text and visibility after every call
        assertEquals(listOf("D-Day" to View.VISIBLE), actual)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_activeOverdue_showsDPlus7() {
        // GIVEN - One real TextView and the reviewed task fixture
        val today = LocalDate.now()
        val view = newView("", View.GONE)
        val tasks = listOf(task(today.minusDays(7)))
        // WHEN - Rebind the same view and record every resulting state
        val actual = bindStates(view, tasks)
        // THEN - Check literal text and visibility after every call
        assertEquals(listOf("D+7" to View.VISIBLE), actual)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_completedDated_showsClear() {
        // GIVEN - One real TextView and the reviewed task fixture
        // This fixture is independent of the wall clock.
        val view = newView("", View.GONE)
        val tasks = listOf(task(LocalDate.parse("2026-10-21"), true))
        // WHEN - Rebind the same view and record every resulting state
        val actual = bindStates(view, tasks)
        // THEN - Check literal text and visibility after every call
        assertEquals(listOf("CLEAR" to View.VISIBLE), actual)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_completedUndated_showsClear() {
        // GIVEN - One real TextView and the reviewed task fixture
        // This fixture is independent of the wall clock.
        val view = newView("", View.GONE)
        val tasks = listOf(task(null, true))
        // WHEN - Rebind the same view and record every resulting state
        val actual = bindStates(view, tasks)
        // THEN - Check literal text and visibility after every call
        assertEquals(listOf("CLEAR" to View.VISIBLE), actual)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_activeUndated_clearsAndHidesStaleLabel() {
        // GIVEN - One real TextView and the reviewed task fixture
        // This fixture is independent of the wall clock.
        val view = newView("stale", View.VISIBLE)
        val tasks = listOf(task(null))
        // WHEN - Rebind the same view and record every resulting state
        val actual = bindStates(view, tasks)
        // THEN - Check literal text and visibility after every call
        assertEquals(listOf("" to View.GONE), actual)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_nullTask_clearsAndHidesStaleLabel() {
        // GIVEN - One real TextView and the reviewed task fixture
        // This fixture is independent of the wall clock.
        val view = newView("stale", View.VISIBLE)
        val tasks = listOf(null)
        // WHEN - Rebind the same view and record every resulting state
        val actual = bindStates(view, tasks)
        // THEN - Check literal text and visibility after every call
        assertEquals(listOf("" to View.GONE), actual)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_nullableInput_clearsWithoutThrowing() {
        // GIVEN - One real TextView and the reviewed task fixture
        // This fixture is independent of the wall clock.
        val view = newView("stale", View.VISIBLE)
        val tasks = listOf(null)
        // WHEN - Rebind the same view and record every resulting state
        val actual = bindStates(view, tasks)
        // THEN - Check literal text and visibility after every call
        assertEquals(listOf("" to View.GONE), actual)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_yesterdayTodayTomorrow_updatesBoundaryLabels() {
        // GIVEN - One real TextView and the reviewed task fixture
        val today = LocalDate.now()
        val view = newView("", View.GONE)
        val tasks = listOf(task(today.minusDays(1)), task(today), task(today.plusDays(1)))
        // WHEN - Rebind the same view and record every resulting state
        val actual = bindStates(view, tasks)
        // THEN - Check literal text and visibility after every call
        assertEquals(listOf("D+1" to View.VISIBLE, "D-Day" to View.VISIBLE, "D-1" to View.VISIBLE), actual)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_undatedCheckUncheck_removesClear() {
        // GIVEN - One real TextView and the reviewed task fixture
        // This fixture is independent of the wall clock.
        val view = newView("", View.GONE)
        val tasks = listOf(task(null), task(null, true), task(null))
        // WHEN - Rebind the same view and record every resulting state
        val actual = bindStates(view, tasks)
        // THEN - Check literal text and visibility after every call
        assertEquals(listOf("" to View.GONE, "CLEAR" to View.VISIBLE, "" to View.GONE), actual)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_datedCheckUncheck_restoresDateLabel() {
        // GIVEN - One real TextView and the reviewed task fixture
        val today = LocalDate.now()
        val view = newView("", View.GONE)
        val tasks = listOf(task(today.plusDays(7)), task(today.plusDays(7), true), task(today.plusDays(7)))
        // WHEN - Rebind the same view and record every resulting state
        val actual = bindStates(view, tasks)
        // THEN - Check literal text and visibility after every call
        assertEquals(listOf("D-7" to View.VISIBLE, "CLEAR" to View.VISIBLE, "D-7" to View.VISIBLE), actual)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_datedUndatedDatedReuse_clearsAndRestoresLabel() {
        // GIVEN - One real TextView and the reviewed task fixture
        val today = LocalDate.now()
        val view = newView("", View.GONE)
        val tasks = listOf(task(today.plusDays(7)), task(null), task(today.plusDays(7)))
        // WHEN - Rebind the same view and record every resulting state
        val actual = bindStates(view, tasks)
        // THEN - Check literal text and visibility after every call
        assertEquals(listOf("D-7" to View.VISIBLE, "" to View.GONE, "D-7" to View.VISIBLE), actual)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_completedNullCompletedReuse_clearsAndRestoresLabel() {
        // GIVEN - One real TextView and the reviewed task fixture
        // This fixture is independent of the wall clock.
        val view = newView("", View.GONE)
        val tasks = listOf(task(null, true), null, task(null, true))
        // WHEN - Rebind the same view and record every resulting state
        val actual = bindStates(view, tasks)
        // THEN - Check literal text and visibility after every call
        assertEquals(listOf("CLEAR" to View.VISIBLE, "" to View.GONE, "CLEAR" to View.VISIBLE), actual)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_invisibleCompleted_becomesVisible() {
        // GIVEN - One real TextView and the reviewed task fixture
        // This fixture is independent of the wall clock.
        val view = newView("stale", View.INVISIBLE)
        val tasks = listOf(task(null, true))
        // WHEN - Rebind the same view and record every resulting state
        val actual = bindStates(view, tasks)
        // THEN - Check literal text and visibility after every call
        assertEquals(listOf("CLEAR" to View.VISIBLE), actual)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_invisibleActiveUndated_clearsAndBecomesGone() {
        // GIVEN - One real TextView and the reviewed task fixture
        // This fixture is independent of the wall clock.
        val view = newView("stale", View.INVISIBLE)
        val tasks = listOf(task(null))
        // WHEN - Rebind the same view and record every resulting state
        val actual = bindStates(view, tasks)
        // THEN - Check literal text and visibility after every call
        assertEquals(listOf("" to View.GONE), actual)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_longFutureInterval_showsTotalDayLabels() {
        // GIVEN - One real TextView and the reviewed task fixture
        val today = LocalDate.now()
        val view = newView("", View.GONE)
        val tasks = listOf(task(today.plusDays(30)), task(today.plusDays(31)), task(today.plusDays(32)))
        // WHEN - Rebind the same view and record every resulting state
        val actual = bindStates(view, tasks)
        // THEN - Check literal text and visibility after every call
        assertEquals(listOf("D-30" to View.VISIBLE, "D-31" to View.VISIBLE, "D-32" to View.VISIBLE), actual)
    }
}
