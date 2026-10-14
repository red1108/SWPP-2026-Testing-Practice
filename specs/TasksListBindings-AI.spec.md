# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `setDueDateLabel(textView: TextView, task: Task?): Unit`

**Intent**: Update the task-list due-date TextView to reflect the current task, including after completion changes and view reuse. Visible labels follow the due-date contract; no label means cleared text and a hidden view.

**Pre-conditions**
- `textView` is a valid Android TextView; call the adapter on the UI thread.
- `task` is a Task or null. Every Task in the fixtures has title = "Task", description = "Description", id = "task-1"; only dueDate and isCompleted vary.
- Date-dependent fixtures assume the device local date remains 2026-10-14 throughout the call.
- Proposed UI convention for human review: an absent label sets text to "" and visibility to View.GONE; slide 53 requires no label but does not specify GONE versus INVISIBLE.
- Repeated calls use the same TextView, because its prior state must not affect the result.

**Post-conditions**
- Success cases
  - An active future task displays the correct label. `[Integration]`
    - Tip: Given a TextView with text = "", visibility = GONE, and Task(dueDate = 2026-10-21, isCompleted = false) / When call setDueDateLabel(textView, task) / Then text = "D-7" and visibility = VISIBLE.
  - An active task due today displays D-Day. `[Integration]`
    - Tip: Given a TextView with text = "", visibility = GONE, and Task(dueDate = 2026-10-14, isCompleted = false) / When call setDueDateLabel(textView, task) / Then text = "D-Day" and visibility = VISIBLE.
  - An active overdue task displays the correct label. `[Integration]`
    - Tip: Given a TextView with text = "", visibility = GONE, and Task(dueDate = 2026-10-07, isCompleted = false) / When call setDueDateLabel(textView, task) / Then text = "D+7" and visibility = VISIBLE.
  - A completed dated task displays CLEAR. `[Integration]`
    - Tip: Given a TextView with text = "", visibility = GONE, and Task(dueDate = 2026-10-21, isCompleted = true) / When call setDueDateLabel(textView, task) / Then text = "CLEAR" and visibility = VISIBLE.
  - A completed undated task displays CLEAR. `[Integration]`
    - Tip: Given a TextView with text = "", visibility = GONE, and Task(dueDate = null, isCompleted = true) / When call setDueDateLabel(textView, task) / Then text = "CLEAR" and visibility = VISIBLE.
  - An active undated task clears and hides the label. `[Integration]`
    - Tip: Given a TextView with text = "stale", visibility = VISIBLE, and Task(dueDate = null, isCompleted = false) / When call setDueDateLabel(textView, task) / Then text = "" and visibility = GONE.
  - A null task clears and hides the label. `[Integration]`
    - Tip: Given a TextView with text = "stale", visibility = VISIBLE, and task = null / When call setDueDateLabel(textView, null) / Then text = "" and visibility = GONE.
- Error cases
  - None. Every typed input satisfying the pre-conditions is valid.
- Edge cases
  - The past/today/future boundary changes text correctly. `[Integration]`
    - Tip: Given one TextView and active Tasks with dueDates = [2026-10-13, 2026-10-14, 2026-10-15] / When bind the Tasks in that order to the same TextView / Then texts = ["D+1", "D-Day", "D-1"] and visibility = VISIBLE after each call.
  - Checking and unchecking an undated task removes CLEAR again. `[Integration]`
    - Tip: Given one TextView initially text = "", visibility = GONE, and Tasks with dueDate = null and completion states = [false, true, false] / When bind those Tasks in order to the same TextView / Then states = [("", GONE), ("CLEAR", VISIBLE), ("", GONE)].
  - Checking and unchecking a dated task restores its D-day label. `[Integration]`
    - Tip: Given one TextView and Tasks with dueDate = 2026-10-21 and completion states = [false, true, false] / When bind those Tasks in order to the same TextView / Then texts = ["D-7", "CLEAR", "D-7"] and visibility = VISIBLE after each call.
  - Reusing a dated row for an undated row removes the old label and allows a new label. `[Integration]`
    - Tip: Given one TextView and active Tasks with dueDates = [2026-10-21, null, 2026-10-21] / When bind those Tasks in order to the same TextView / Then states = [("D-7", VISIBLE), ("", GONE), ("D-7", VISIBLE)].
  - Clearing a binding and rebinding a task updates both text and visibility. `[Integration]`
    - Tip: Given one TextView and bindings = [Task(dueDate = null, isCompleted = true), null, Task(dueDate = null, isCompleted = true)] / When bind those values in order to the same TextView / Then states = [("CLEAR", VISIBLE), ("", GONE), ("CLEAR", VISIBLE)].

**Undefined behavior** (optional)
- A device date change during a single fixture is outside this draft contract; tests should use a stable clock strategy.
- Picker behavior, persistence, and full-screen navigation are outside this adapter contract.
