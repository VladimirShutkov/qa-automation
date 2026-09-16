# QA Automation Portfolio — руководство для Codex

## Проект

Публичное портфолио Senior QA Automation Engineer.

Стек: Java 21, Maven, JUnit 5, Playwright, REST Assured, JSON Schema Validator, Allure, GitHub Actions.

Тестируемые системы:
- SauceDemo — UI
- Restful Booker — API

## Архитектура

- `config` — конфигурация и окружение
- `ui` — Playwright, Page Objects и UI-компоненты
- `api` — REST-клиенты и specifications
- `models` — DTO и domain models
- `utils` — stateless utilities
- `src/test/.../ui|api|integration` — тесты
- `support` — fixtures, extensions и helpers
- `test-data` — тестовые данные
- `schemas` — JSON schemas

## Работа с проектом

- Работать только с файлами, относящимися к текущей задаче.
- Не сканировать весь репозиторий без необходимости.
- Использовать существующую архитектуру и паттерны.
- Вносить минимальные изменения; не изменять unrelated code.
- Не добавлять зависимости или инструменты без необходимости.
- Не хранить и не передавать credentials, tokens и другие секреты.
- Сначала запускать минимальную необходимую проверку; полный `mvn clean test` — только при необходимости.
- Не повторять дорогие команды без причины.

## Git

- Сохранять unrelated изменения пользователя.
- Commit выполнять только по явному запросу.
- Push, force-push, rewrite history, изменение remote и удаление файлов — только по явному разрешению.
- Не добавлять generated output и IDE-файлы в Git.