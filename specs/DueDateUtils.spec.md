# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Produce the task-list label from completion status and the total number of calendar days between today and the optional due date. Completed tasks always show CLEAR; an active task without a due date has no label.

**Pre-conditions**
- `today` is an explicit valid `LocalDate`; `dueDate` is a valid `LocalDate` or `null`.
- Dates are calendar dates, independent of elapsed hours, time zones, and daylight saving.
- The oracle is the feature intent on tutorial slide 53 and the course feature PR description, not the implementation.

**Post-conditions**
- Success cases
  - An active task due in N > 0 calendar days returns exactly D-N. `[Unit]`
    - Tip: Given dueDate = 2026-10-21, today = 2026-10-14, isCompleted = false / When call dueDateLabel(dueDate, today, isCompleted) / Then return "D-7". Tested by `DueDateUtilsTest.dueDateLabel_futureWeek_returnsDMinus7`.
  - An active task due today returns exactly D-Day. `[Unit]`
    - Tip: Given dueDate = 2026-10-14, today = 2026-10-14, isCompleted = false / When call dueDateLabel(dueDate, today, isCompleted) / Then return "D-Day". Tested by `DueDateUtilsTest.dueDateLabel_today_returnsDDay`.
  - An active task overdue by N > 0 calendar days returns exactly D+N. `[Unit]`
    - Tip: Given dueDate = 2026-10-07, today = 2026-10-14, isCompleted = false / When call dueDateLabel(dueDate, today, isCompleted) / Then return "D+7". Tested by `DueDateUtilsTest.dueDateLabel_overdueWeek_returnsDPlus7`.
  - An active task without a due date returns the empty string. `[Unit]`
    - Tip: Given dueDate = null, today = 2026-10-14, isCompleted = false / When call dueDateLabel(dueDate, today, isCompleted) / Then return "". Tested by `DueDateUtilsTest.dueDateLabel_activeWithoutDate_returnsEmpty`.
  - Completion overrides a due date. `[Unit]`
    - Tip: Given dueDate = 2026-10-21, today = 2026-10-14, isCompleted = true / When call dueDateLabel(dueDate, today, isCompleted) / Then return "CLEAR". Tested by `DueDateUtilsTest.dueDateLabel_completedWithDate_returnsClear`.
  - A completed task without a due date also returns CLEAR. `[Unit]`
    - Tip: Given dueDate = null, today = 2026-10-14, isCompleted = true / When call dueDateLabel(dueDate, today, isCompleted) / Then return "CLEAR". Tested by `DueDateUtilsTest.dueDateLabel_completedWithoutDate_returnsClear`.
- Error cases
  - None. Every typed input satisfying the pre-conditions is valid. `[Unit]`
    - Tip: Given dueDate = null, today = 2026-10-14, isCompleted = false / When call dueDateLabel(dueDate, today, isCompleted) / Then return "" without throwing. Tested by `DueDateUtilsTest.dueDateLabel_nullableInput_returnsEmptyWithoutThrowing`.
- Edge cases
  - The past/today/future boundary is -1, 0, and +1 calendar day. `[Unit]`
    - Tip: Given today = 2026-10-14, isCompleted = false, dueDates = [2026-10-13, 2026-10-14, 2026-10-15] / When call dueDateLabel separately for each dueDate / Then return ["D+1", "D-Day", "D-1"], respectively. Tested by `DueDateUtilsTest.dueDateLabel_yesterdayTodayTomorrow_returnsBoundaryLabels`.
  - A month transition counts every calendar day. `[Unit]`
    - Tip: Given today = 2026-01-31, isCompleted = false, dueDates = [2026-01-31, 2026-02-01, 2026-02-02] / When call dueDateLabel separately for each dueDate / Then return ["D-Day", "D-1", "D-2"], respectively. Tested by `DueDateUtilsTest.dueDateLabel_monthEnd_returnsCalendarDayLabels`.
  - A whole-month distance is not zero days. `[Unit]`
    - Tip: Given today = 2026-01-15, isCompleted = false, dueDates = [2026-02-14, 2026-02-15, 2026-02-16] / When call dueDateLabel separately for each dueDate / Then return ["D-30", "D-31", "D-32"], respectively. Tested by `DueDateUtilsTest.dueDateLabel_fullMonthAndAdjacentDates_returnsTotalDayLabels`.
  - A year transition counts every calendar day. `[Unit]`
    - Tip: Given today = 2026-12-31, isCompleted = false, dueDates = [2026-12-31, 2027-01-01, 2027-01-02] / When call dueDateLabel separately for each dueDate / Then return ["D-Day", "D-1", "D-2"], respectively. Tested by `DueDateUtilsTest.dueDateLabel_yearEnd_returnsCalendarDayLabels`.
  - A leap day contributes one calendar day. `[Unit]`
    - Tip: Given today = 2028-02-28, isCompleted = false, dueDates = [2028-02-28, 2028-02-29, 2028-03-01] / When call dueDateLabel separately for each dueDate / Then return ["D-Day", "D-1", "D-2"], respectively. Tested by `DueDateUtilsTest.dueDateLabel_leapDay_returnsCalendarDayLabels`.
  - Completion changes the result even when no due date exists. `[Unit]`
    - Tip: Given dueDate = null, today = 2026-10-14, completion states = [false, true, false] / When call dueDateLabel for each completion state / Then return ["", "CLEAR", ""], respectively. Tested by `DueDateUtilsTest.dueDateLabel_undatedCompletionSequence_returnsEmptyClearEmpty`.

  - Completed tasks ignore past, present, and future due dates equally. `[Unit]`
    - Tip: Given today = 2026-10-14, isCompleted = true, dueDates = [2026-10-13, 2026-10-14, 2026-10-15] / When call dueDateLabel for each dueDate / Then return ["CLEAR", "CLEAR", "CLEAR"]. Tested by `DueDateUtilsTest.dueDateLabel_completedPastTodayFuture_returnsClear`.
  - A full overdue month also counts total calendar days, including adjacent values. `[Unit]`
    - Tip: Given today = 2026-02-15, isCompleted = false, dueDates = [2026-01-14, 2026-01-15, 2026-01-16] / When call dueDateLabel for each dueDate / Then return ["D+32", "D+31", "D+30"]. Tested by `DueDateUtilsTest.dueDateLabel_overdueMonthAndAdjacentDates_returnsTotalDayLabels`.
  - Whole-year future intervals include all days, including the days next to the anniversary. `[Unit]`
    - Tip: Given today = 2026-12-15, isCompleted = false, dueDates = [2027-12-14, 2027-12-15, 2027-12-16] / When call dueDateLabel for each dueDate / Then return ["D-364", "D-365", "D-366"]. Tested by `DueDateUtilsTest.dueDateLabel_futureYearAndAdjacentDates_returnsTotalDayLabels`.
  - Whole-year overdue intervals use the same calendar-day definition. `[Unit]`
    - Tip: Given today = 2027-12-15, isCompleted = false, dueDates = [2026-12-14, 2026-12-15, 2026-12-16] / When call dueDateLabel for each dueDate / Then return ["D+366", "D+365", "D+364"]. Tested by `DueDateUtilsTest.dueDateLabel_overdueYearAndAdjacentDates_returnsTotalDayLabels`.
  - Overdue leap-day intervals count February 29. `[Unit]`
    - Tip: Given today = 2028-03-01, isCompleted = false, dueDates = [2028-02-28, 2028-02-29, 2028-03-01] / When call dueDateLabel for each dueDate / Then return ["D+2", "D+1", "D-Day"]. Tested by `DueDateUtilsTest.dueDateLabel_overdueLeapDay_returnsCalendarDayLabels`.
  - A non-leap February has no extra day. `[Unit]`
    - Tip: Given today = 2027-02-28, isCompleted = false, dueDates = [2027-02-28, 2027-03-01, 2027-03-02] / When call dueDateLabel for each dueDate / Then return ["D-Day", "D-1", "D-2"]. Tested by `DueDateUtilsTest.dueDateLabel_nonLeapFebruary_returnsCalendarDayLabels`.

**Undefined behavior** (optional)
- None within the typed input domain; this function does not parse strings or read the system clock.
