# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `setDueDateLabel(textView: TextView, task: Task?): Unit`

**Intent**: Update the task-list due-date TextView to reflect the current task, including after completion changes and view reuse. Visible labels follow the due-date contract; no label means cleared text and a hidden view.

**Pre-conditions**
- `textView` is a valid Android TextView; call the adapter on the UI thread.
- `task` is a Task or null. Every Task in the fixtures has title = "Task", description = "Description", id = "task-1"; only dueDate and isCompleted vary.
- Exact examples below use reference date 2026-10-14. For automated runs capture T = LocalDate.now() on the UI thread, then translate every dated active-task input by its signed calendar-day offset from 2026-10-14. Thus example 2026-10-21 is exactly T.plusDays(7), with expected text still "D-7". This translation is a fixture rule, not a call to the production label function.
- The local date must remain stable during each short UI-thread fixture; the test must not require that the execution date equal 2026-10-14.
- Human-reviewed UI convention: an absent label sets text to "" and visibility to View.GONE; slide 53 requires no label but does not specify GONE versus INVISIBLE.
- Repeated calls use the same TextView, because its prior state must not affect the result.

**Post-conditions**
- Success cases
  - An active future task displays the correct label. `[Integration]`
    - Tip: Given a TextView with text = "", visibility = GONE, and Task(dueDate = 2026-10-21, isCompleted = false) / When call setDueDateLabel(textView, task) / Then text = "D-7" and visibility = VISIBLE. Tested by `TasksListBindingsTest.setDueDateLabel_activeFuture_showsDMinus7`.
  - An active task due today displays D-Day. `[Integration]`
    - Tip: Given a TextView with text = "", visibility = GONE, and Task(dueDate = 2026-10-14, isCompleted = false) / When call setDueDateLabel(textView, task) / Then text = "D-Day" and visibility = VISIBLE. Tested by `TasksListBindingsTest.setDueDateLabel_activeToday_showsDDay`.
  - An active overdue task displays the correct label. `[Integration]`
    - Tip: Given a TextView with text = "", visibility = GONE, and Task(dueDate = 2026-10-07, isCompleted = false) / When call setDueDateLabel(textView, task) / Then text = "D+7" and visibility = VISIBLE. Tested by `TasksListBindingsTest.setDueDateLabel_activeOverdue_showsDPlus7`.
  - A completed dated task displays CLEAR. `[Integration]`
    - Tip: Given a TextView with text = "", visibility = GONE, and Task(dueDate = 2026-10-21, isCompleted = true) / When call setDueDateLabel(textView, task) / Then text = "CLEAR" and visibility = VISIBLE. Tested by `TasksListBindingsTest.setDueDateLabel_completedDated_showsClear`.
  - A completed undated task displays CLEAR. `[Integration]`
    - Tip: Given a TextView with text = "", visibility = GONE, and Task(dueDate = null, isCompleted = true) / When call setDueDateLabel(textView, task) / Then text = "CLEAR" and visibility = VISIBLE. Tested by `TasksListBindingsTest.setDueDateLabel_completedUndated_showsClear`.
  - An active undated task clears and hides the label. `[Integration]`
    - Tip: Given a TextView with text = "stale", visibility = VISIBLE, and Task(dueDate = null, isCompleted = false) / When call setDueDateLabel(textView, task) / Then text = "" and visibility = GONE. Tested by `TasksListBindingsTest.setDueDateLabel_activeUndated_clearsAndHidesStaleLabel`.
  - A null task clears and hides the label. `[Integration]`
    - Tip: Given a TextView with text = "stale", visibility = VISIBLE, and task = null / When call setDueDateLabel(textView, null) / Then text = "" and visibility = GONE. Tested by `TasksListBindingsTest.setDueDateLabel_nullTask_clearsAndHidesStaleLabel`.
- Error cases
  - None. Every typed input satisfying the pre-conditions is valid. `[Integration]`
    - Tip: Given a TextView with text = "stale", visibility = VISIBLE, and task = null / When call setDueDateLabel(textView, null) / Then text = "", visibility = GONE, and no exception is thrown. Tested by `TasksListBindingsTest.setDueDateLabel_nullableInput_clearsWithoutThrowing`.
- Edge cases
  - The past/today/future boundary changes text correctly. `[Integration]`
    - Tip: Given one TextView and active Tasks with dueDates = [2026-10-13, 2026-10-14, 2026-10-15] / When bind the Tasks in that order to the same TextView / Then texts = ["D+1", "D-Day", "D-1"] and visibility = VISIBLE after each call. Tested by `TasksListBindingsTest.setDueDateLabel_yesterdayTodayTomorrow_updatesBoundaryLabels`.
  - Checking and unchecking an undated task removes CLEAR again. `[Integration]`
    - Tip: Given one TextView initially text = "", visibility = GONE, and Tasks with dueDate = null and completion states = [false, true, false] / When bind those Tasks in order to the same TextView / Then states = [("", GONE), ("CLEAR", VISIBLE), ("", GONE)]. Tested by `TasksListBindingsTest.setDueDateLabel_undatedCheckUncheck_removesClear`.
  - Checking and unchecking a dated task restores its D-day label. `[Integration]`
    - Tip: Given one TextView and Tasks with dueDate = 2026-10-21 and completion states = [false, true, false] / When bind those Tasks in order to the same TextView / Then texts = ["D-7", "CLEAR", "D-7"] and visibility = VISIBLE after each call. Tested by `TasksListBindingsTest.setDueDateLabel_datedCheckUncheck_restoresDateLabel`.
  - Reusing a dated row for an undated row removes the old label and allows a new label. `[Integration]`
    - Tip: Given one TextView and active Tasks with dueDates = [2026-10-21, null, 2026-10-21] / When bind those Tasks in order to the same TextView / Then states = [("D-7", VISIBLE), ("", GONE), ("D-7", VISIBLE)]. Tested by `TasksListBindingsTest.setDueDateLabel_datedUndatedDatedReuse_clearsAndRestoresLabel`.
  - Clearing a binding and rebinding a task updates both text and visibility. `[Integration]`
    - Tip: Given one TextView and bindings = [Task(dueDate = null, isCompleted = true), null, Task(dueDate = null, isCompleted = true)] / When bind those values in order to the same TextView / Then states = [("CLEAR", VISIBLE), ("", GONE), ("CLEAR", VISIBLE)]. Tested by `TasksListBindingsTest.setDueDateLabel_completedNullCompletedReuse_clearsAndRestoresLabel`.

  - An initially INVISIBLE view becomes visible when it receives a label. `[Integration]`
    - Tip: Given a TextView with text = "stale", visibility = INVISIBLE, and Task(dueDate = null, isCompleted = true) / When call setDueDateLabel(textView, task) / Then text = "CLEAR" and visibility = VISIBLE. Tested by `TasksListBindingsTest.setDueDateLabel_invisibleCompleted_becomesVisible`.
  - An initially INVISIBLE view is cleared and removed when it receives no label. `[Integration]`
    - Tip: Given a TextView with text = "stale", visibility = INVISIBLE, and Task(dueDate = null, isCompleted = false) / When call setDueDateLabel(textView, task) / Then text = "" and visibility = GONE. Tested by `TasksListBindingsTest.setDueDateLabel_invisibleActiveUndated_clearsAndBecomesGone`.
  - A long future interval is displayed as a total-day label. `[Integration]`
    - Tip: Given reference today = 2026-10-14, one TextView, and active Tasks with dueDates = [2026-11-13, 2026-11-14, 2026-11-15] / When bind the Tasks in order using the fixture translation rule / Then texts = ["D-30", "D-31", "D-32"] and visibility = VISIBLE after each call. Tested by `TasksListBindingsTest.setDueDateLabel_longFutureInterval_showsTotalDayLabels`.

**Undefined behavior** (optional)
- A device date change during a single fixture is outside this contract; tests should use a stable clock strategy.
- Picker behavior, persistence, and full-screen navigation are outside this adapter contract.
