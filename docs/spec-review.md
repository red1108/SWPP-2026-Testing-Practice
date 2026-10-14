# Tutorial 07 spec review

Status: human-reviewed on 2026-10-14 in this chat. After the detailed three-part explanation, the student replied: '좋아. 그렇게 해.' The student accepted calendar-day counting, completion transitions, and empty text plus View.GONE, including null-task clearing.

The unchanged initial drafts are `specs/DueDateUtils-AI.spec.md` and
`specs/TasksListBindings-AI.spec.md`. They were drafted earlier in this chat and
preserved across the fork recreation. Both refer to the same course feature
commit, 7e091a7. The canonical specs are the human-reviewed refined versions.

## Oracle

The feature intent on tutorial slide 53 and the original course PR description
are the behavioral oracle. Expected numeric values are explicit examples of
calendar-day counting. They are not calculated by calling the implementation
under test. The two targets are dueDateLabel and setDueDateLabel.

## Refinements and reasons

| Refinement | Reason |
| --- | --- |
| Add completed tasks with yesterday/today/tomorrow due dates | The initial draft only had a future dated completion case; CLEAR must override all date categories. |
| Add overdue full-month cases, including the days on both sides | The initial full-month case only looked forward. Check the sign and total day count in the overdue direction too. |
| Add full-year cases in both directions with adjacent dates | A one-day year crossing does not establish behavior over a whole year. |
| Add overdue leap-day and ordinary February examples | Check reverse direction and ensure the leap-year rule is not applied to every February. |
| Replace the adapter's fixed execution date with an explicit fixture translation | CI does not run forever on 2026-10-14. Translate exact reference-date examples by a captured UI-thread today; keep literal expected labels. The fixture still assumes the date is stable during the short call sequence. |
| Add initially INVISIBLE view cases | Initial GONE and VISIBLE fixtures do not cover every Android visibility state. Nonempty labels must become visible; empty labels follow the reviewed hiding convention. |
| Add a 30/31/32-day adapter case | Verify total-day behavior is displayed by the adapter as well as returned by the pure function. |
| Add a tagged no-error Tip to each error section | Both typed interfaces accept their nullable input. Make the no-exception expectation executable and satisfy the repository's Tip mapping rule. |

## Decisions confirmed by the student

- No-label behavior is confirmed as text = empty string and visibility = View.GONE.
  The handout specifies no label, but does not specify GONE versus INVISIBLE.
- A null task should clear a recycled view. This is an explicit adapter contract
  extending the visible-task requirements, rather than a direct quote from the
  handout.
- Same-view completion transitions and rebinding must clear stale content.

The initial AI drafts already included a 31-day calendar case and the undated
false/true/false binding sequence. Therefore the refinements should be described
as broader coverage and clearer fixtures, not as the first discovery of the two
handout bugs.

## Baseline evidence

At 7f0ae8f, before adding due-date tests, the three statistics tests and one
active-task-detail Espresso test passed locally. Both CI jobs passed:
https://github.com/red1108/SWPP-2026-Testing-Practice/actions/runs/38553812373

Passing those tests does not validate due-date behavior; the baseline suite has
no assertions for either target function.
