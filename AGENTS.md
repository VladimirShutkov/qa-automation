# QA Automation Portfolio — Contributor Guide

## Purpose

This repository is a public Senior QA Automation portfolio. It demonstrates maintainable UI, REST API, integration, positive, negative, boundary, schema-validation, reporting, CI/CD, and carefully scoped AI-assisted QA practices. It is not a collection of isolated tutorial tests.

## Technology baseline

- Java 21 (Maven compiler target)
- Maven
- JUnit 5
- Playwright for UI automation
- REST Assured and JSON Schema Validator for API automation
- Allure for reporting
- GitHub Actions for CI

Target systems are SauceDemo (UI) and Restful Booker (API). Do not add external dependencies or tools without a clear architectural need.

## Architecture

- `src/main/java/io/github/vladimirshutkov/qaa/config`: configuration and environment access.
- `ui`: Playwright lifecycle, Page Objects, and reusable UI components.
- `api`: REST clients and reusable request/response specifications.
- `models`: immutable request/response DTOs and domain models.
- `utils`: small, stateless utilities only.
- `src/test/java/.../ui`, `api`, and `integration`: executable checks separated by test level.
- `support`: JUnit extensions, fixtures, and test-only assertions; it must not contain scenarios.
- `src/test/resources/test-data`: versioned non-secret test data.
- `src/test/resources/schemas`: versioned API contract schemas.

Keep system-specific details at the edge (pages and clients). Tests express business intent; they do not implement browser or HTTP mechanics.

## UI test rules

- Use Page Objects/components with meaningful public actions; do not expose raw selectors to test classes.
- Prefer resilient locators: role, label, test id, then stable semantic CSS. Avoid XPath and positional selectors unless unavoidable.
- A scenario covers one observable behavior and owns its test data.
- Tests must be isolated, parallel-safe where feasible, and clean up state they create.

## API test rules

- Centralize base URLs, authentication, request specifications, and endpoint interaction in clients/configuration.
- Keep request and response payloads in typed models when that improves clarity; do not duplicate JSON literals across tests.
- Cover success, validation errors, authorization, boundaries, and contract/schema behavior deliberately.
- Assert status, relevant headers, body contract, and business-critical fields; do not assert volatile fields without purpose.

## Test data and assertions

- Never commit credentials, tokens, personal data, or environment-specific secrets. Use environment variables or CI secrets.
- Test data must be minimal, named by intent, deterministic, and kept separate from test logic.
- Use explicit JUnit/REST Assured assertions with diagnostic messages where context is otherwise unclear.
- Assert outcomes, not implementation details. A single scenario may have several related assertions, but avoid unrelated checks.

## Synchronization

- Never use `Thread.sleep` or arbitrary retry loops.
- Use Playwright auto-waiting and explicit condition-based waits only when auto-waiting is insufficient.
- API polling must be rare, bounded, and have a documented eventual-consistency reason.

## Naming and readability

- Classes use PascalCase; methods and fields use camelCase; constants use UPPER_SNAKE_CASE.
- Test class names end in `Test`; test method names describe behavior, for example `shouldRejectBookingWhenCheckoutPrecedesCheckin`.
- Use Arrange-Act-Assert (or Given-When-Then) with short, intentional sections.
- Prefer small focused methods, clear names, and one source of truth. Extract reusable behavior instead of copy/paste; do not create abstractions for one-off code.

## Running and verification

- Validate the baseline with `mvn clean test`.
- Run targeted suites with Maven/JUnit selectors only after tests exist.
- Generate an Allure report with `mvn allure:report` after a test run.
- Before proposing changes, compile/test the smallest relevant scope and report commands that could not run.

## Git workflow

- Inspect `git status` before and after work. Preserve unrelated user changes.
- Make focused, reviewable commits only when explicitly requested.
- Do not change remotes, force-push, push, rewrite history, or delete files without explicit user authorization.
- Keep generated output (`target/`, Allure results/reports, IDE files) out of version control.

## AI-assisted QA

- AI may help draft ideas, data matrices, selectors, and documentation, but a human verifies all generated code and assertions.
- Do not send secrets, proprietary data, or production customer data to AI tools.
- Treat AI output as untrusted until reviewed against product behavior, security expectations, and project conventions.
