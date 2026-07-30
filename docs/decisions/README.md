# Decisions

This folder contains **decision records**: snapshots of significant design choices, including the context, trade-offs explored, and resolution.

**Key rule: APPEND-ONLY.** Once a decision is recorded, it is never rewritten. If a prior decision is superseded by new context, record a new decision that references and replaces the old one, rather than editing history.

## When to Record a Decision

Record a decision when:
- A design choice affects multiple modules or has long-term consequences
- Trade-offs were explored and the choice was deliberate
- Future developers might wonder "why did we do it this way?" (e.g., after an incident or change in constraints)

Do **not** record trivial naming choices or one-off local refactors.

## Decision Format

Use this structure:

```markdown
# 000X — Title (One sentence)

**Status:** Accepted | Superseded (by 000Y) | Deprecated

**Context**
Brief overview of the problem and constraints.

**Options Considered**
- Option A: pros/cons
- Option B: pros/cons
- ...

**Decision**
The chosen option and why.

**Consequences**
What follows from this choice (positive and negative).

**References**
Links to related decisions or architecture docs.
```

## Decisions

Start recording decisions as significant choices arise during development.