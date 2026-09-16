---
name: create-pr
description: Create a GitHub Pull Request for the current pushed feature branch after analysing its changes against main. Use for "create PR", "создай PR", or "/pr" requests; do not use to merge or modify source code.
---

# Create Pull Request

Create one PR from the current branch to `main` using GitHub CLI. Do not make commits, push changes, merge a PR, or alter application source code.

## Preconditions

Before composing a PR, check all of the following:

- `git branch --show-current` returns a non-empty branch name other than `main`.
- `git status --porcelain` is empty.
- The current branch is pushed to `origin` and `origin/<current-branch>` resolves. Confirm the remote branch contains `HEAD` (for example, compare `HEAD` with `origin/<current-branch>`).
- `gh --version` succeeds.
- `gh auth status` succeeds.

Stop at the first unmet prerequisite. If `gh` is unavailable, tell the user to run exactly `winget install --id GitHub.cli`. If authentication is unavailable, tell the user to run exactly `gh auth login`. Do not attempt a browser login on the user's behalf. For other failed prerequisites, state the failed condition and the corrective action; do not create the PR.

## Analyse the branch

Collect the actual branch changes with:

```powershell
git diff main...HEAD
git log main..HEAD --oneline
```

Use both the diff and commit history to generate the content. Never use a feature-specific, prewritten description. Exclude credentials, tokens, secrets, environment-variable values, and private reasoning or debugging material from all generated text.

Generate a concise English PR title in Conventional Commits form (`feat:`, `fix:`, `test:`, `ci:`, `docs:`, `refactor:`, `chore:` as appropriate). It must describe the real change and must not be a generic title such as `Update files`.

Create the description in this exact order, omitting `## Notes` when it has no material content:

```markdown
## Summary

<English summary based on the diff and commits.>

## Что сделано

<Краткое русское описание тех же изменений.>

## Changes

- <Main technical changes in English.>

## Изменения

- <Те же основные изменения на русском.>

## Testing

<Only checks actually run and their observed results.>

## Notes

<Only important additional information.>
```

Do not invent test outcomes. Include only commands and results confirmed in the current task context. If no tests were run, write `Not run (not requested).` in `## Testing`.

## Create and report

Write the description to a unique temporary file. In PowerShell, use a temp-file API such as `[System.IO.Path]::GetTempFileName()`; do not use inline multiline `--body` text. Create the PR with:

```powershell
gh pr create --base main --head <current-branch> --title "<generated-title>" --body-file <temporary-description-file>
```

Capture the URL printed by `gh pr create`. Remove the temporary description file in a `finally` block, including when PR creation fails. Do not retry a failed creation unless the user explicitly asks.

On success, report the PR title, PR URL, and a brief statement of which commit(s) and diff scope were used to form it.
