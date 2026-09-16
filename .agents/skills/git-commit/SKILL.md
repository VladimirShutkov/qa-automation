---
name: git-commit
description: Использовать, когда пользователь просит создать Git commit или подготовить commit message.
---

# Git Commit

Использовать Conventional Commits:

`<type>[optional scope]: <description>`

Типы:
- `feat` — новая функциональность
- `fix` — исправление
- `test` — тесты
- `refactor` — рефакторинг
- `docs` — документация
- `build` — build/dependencies
- `ci` — CI
- `chore` — прочие изменения

## Workflow

1. Проверить `git status --short`.
2. Анализировать только изменённые файлы, относящиеся к commit.
3. Если есть staged changes, использовать `git diff --staged`; иначе проверить необходимый diff.
4. Не добавлять unrelated файлы.
5. Проверить отсутствие секретов.
6. Сформировать короткий Conventional Commit message.
7. Выполнить commit только по явному запросу пользователя.

Commit message:
- imperative mood;
- описание до 72 символов;
- один логический change на commit.