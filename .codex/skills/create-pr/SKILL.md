---
name: create-pr
description: Create a GitHub Pull Request for the current pushed feature branch after analysing its changes against main. Use for "create PR", "создай PR", or "/pr" requests; do not use to merge or modify source code.
---

# Create Pull Request

Create one PR from the current branch to `main` using GitHub CLI. Do not make commits, push changes, merge a PR, or alter application source code. Never output tokens, credentials, or secrets.

## Preconditions

Before composing a PR, check all of the following locally inside the Codex sandbox:

- `git branch --show-current` returns a non-empty branch name other than `main`.
- `git status --porcelain` is empty.
- The current branch is pushed to `origin` and `origin/<current-branch>` resolves. Confirm the remote branch contains `HEAD` (for example, compare `HEAD` with `origin/<current-branch>`).
- `where.exe gh` finds GitHub CLI and `gh --version` succeeds.

Run these local analysis commands in the sandbox:

```powershell
git status --porcelain
git branch --show-current
git diff main...HEAD
git log main..HEAD --oneline
```

Do not use `gh auth status` inside the Codex sandbox as a required authentication-validity check: a sandbox proxy or network restriction can make it report an invalid token incorrectly. Do not use `gh api user` as a sandbox authentication prerequisite.

Treat failures distinctly and stop at the first unmet local prerequisite:

- **GitHub CLI missing:** if `where.exe gh` cannot find `gh`, tell the user to run exactly `winget install --id GitHub.cli`.
- **Git prerequisite failed:** state the failed condition and the corrective action; do not create the PR.
- **Codex sandbox/network/proxy restriction:** errors such as `proxyconnect`, connection refusal to a local proxy (for example `127.0.0.1:9`), DNS failures, or denied network permission do not indicate invalid GitHub authentication.

## Analyse the branch

Use only the actual `git diff main...HEAD` and `git log main..HEAD --oneline` output to generate PR content. Never use a feature-specific, prewritten description. Exclude credentials, tokens, secrets, environment-variable values, and private reasoning or debugging material from all generated text.

Generate a concise English-only PR title in Conventional Commits form (`feat:`, `fix:`, `test:`, `ci:`, `docs:`, `refactor:`, `chore:` as appropriate). It must describe the real change and must not be generic, such as `Update files`.

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

Write the description to a unique temporary file. In PowerShell, use a temp-file API such as `[System.IO.Path]::GetTempFileName()`; do not use inline multiline `--body` text. Keep the file until PR creation succeeds or until it is handed off to the user.

Build this command using the actual branch, English title, and temporary body-file path:

```powershell
gh pr create --base main --head <current-branch> --title "<generated-title>" --body-file "<temporary-description-file>"
```

`gh pr create` is a network operation. First attempt it with network/escalated permission when Codex can request that permission. Use the generated body file and do not expose its contents beyond the PR request.

- If the elevated command succeeds, capture its URL, remove the temporary description file in a `finally` block, and report the PR title, URL, and the commits and diff scope used.
- If the elevated command reports a genuine GitHub authentication error (not a sandbox, proxy, or connectivity error), report it clearly as an authentication error without exposing credentials. Do not create the PR.
- If network permission cannot be obtained, or GitHub API access is blocked by the Codex sandbox, proxy, or network, do not report that the token is invalid and do not suggest `gh auth login`. Preserve the temporary body file and show the user exactly one ready-to-copy PowerShell command using its actual path:

```powershell
gh pr create --base main --head <current-branch> --title "<generated-title>" --body-file "<temporary-description-file>"
```

The user can run that command outside Codex. Do not retry a failed creation unless the user explicitly asks.
