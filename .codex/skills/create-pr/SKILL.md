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
- `where.exe gh` finds GitHub CLI and `gh --version` succeeds. Run these local checks inside the Codex sandbox.
- `gh auth status` confirms an active, valid GitHub CLI authentication. Run this check in the sandbox first; do not display its token or other credentials.

Run local Git commands (`git status`, `git branch`, `git diff`, and `git log`) and the CLI discovery/version checks (`where.exe gh`, `gh --version`) inside the sandbox.

Treat failures distinctly and stop at the first unmet prerequisite:

- **GitHub CLI absent:** if `where.exe gh` cannot find `gh`, tell the user to run exactly `winget install --id GitHub.cli`.
- **Invalid or missing authentication:** only when `gh auth status` explicitly reports no active login, an invalid token, expired/revoked credentials, or another authentication-specific failure, tell the user to run exactly `gh auth login`. Do not attempt a browser login on the user's behalf.
- **Sandbox/proxy network block:** errors such as `proxyconnect`, connection refusal to a local proxy (for example `127.0.0.1:9`), DNS/connectivity failures, or other network-access errors do **not** mean the token is invalid. Request permission to rerun the necessary network command outside the restricted sandbox, with `sandbox_permissions: "require_escalated"` and a concise user-facing justification. Do not ask the user to reauthenticate based on such an error.

Use `gh api user --jq ".login"` as the network-backed GitHub CLI verification when needed. If it fails with a sandbox/proxy network block, rerun that same command with the required elevated sandbox permission. If the elevated command reports an authentication-specific failure, handle it as invalid authentication; otherwise report the actual network failure and do not create the PR.

For other failed prerequisites, state the failed condition and the corrective action; do not create the PR.

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

`gh pr create` is a network operation. If it is blocked by the sandbox or proxy, request permission to rerun it outside the restricted sandbox with `sandbox_permissions: "require_escalated"` and a concise justification that asks to create this PR through GitHub. Preserve `--body-file` and do not expose the temporary file contents beyond the PR request. If the elevated command reports a genuine authentication error, report it as such; if it reports a network error, report that network error without calling the token invalid.

Capture the URL printed by `gh pr create`. Remove the temporary description file in a `finally` block, including when PR creation fails. Do not retry a failed creation unless the user explicitly asks.

On success, report the PR title, PR URL, and a brief statement of which commit(s) and diff scope were used to form it.
