# Investigations

**Working-state documents** tied to open backlog items. These are temporary analyses that live in git while a ticket is active, then get folded into [`../architecture/`](../architecture/) or [`../conventions/`](../conventions/) and deleted when the work lands.

**Key rule:** When you complete an investigation:
1. Extract findings into the relevant permanent home (architecture, conventions, or a decision record)
2. Delete the investigation file
3. Close the associated backlog item

This prevents investigations from rotting in the repository as stale analysis.

## Creating an Investigation

Name the file after the backlog item: `BACKLOG-42-extended-recon-system.md` (if you're using BACKLOG-style IDs) or link to your backlog.md item.

Structure:

```markdown
# BACKLOG-N — Title

**Status:** Open | Complete

**Question/Goal**
What are we trying to figure out?

**Context**
What prompted this investigation?

**Findings**
Current understanding and open questions.

**Next Steps**
What remains to be determined?
```

## Current Investigations

(Add investigations here as they are created.)