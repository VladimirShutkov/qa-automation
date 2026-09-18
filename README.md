# QA Automation Portfolio

A focused portfolio project demonstrating maintainable UI and REST API test automation, diagnostic reporting, and CI/CD. It uses a small set of representative scenarios to show test design, isolation, configuration, and observability rather than maximizing test count.

## Technology Stack

- Java 21 and Maven
- JUnit 5
- Playwright for browser automation
- REST Assured and JSON Schema Validator for API automation
- Allure for test reporting
- GitHub Actions and GitHub Pages for CI and report publication

## Test Targets

- **UI:** [SauceDemo](https://www.saucedemo.com/)
- **API:** [Restful Booker](https://restful-booker.herokuapp.com/)

Both are public demo applications/APIs used here specifically for portfolio automation.

## What Is Demonstrated

**UI automation** covers valid login, invalid credentials, locked-out users, logout, and adding a product to the cart. Tests use Page Objects, Playwright's auto-waiting web-first assertions, and an isolated browser context for every test. A failure screenshot is attached to Allure before the UI session is closed.

**API automation** covers booking retrieval, creation, authenticated update and deletion, non-existent resources, and rejected unauthenticated updates. It verifies persisted state after updates, cleans up created bookings, validates representative responses against JSON Schemas, and parses booking dates as strict Java `LocalDate` values. The REST client applies finite connection/socket timeouts and adds sanitized request/response diagnostics to Allure.

## Project Structure

```text
src/
├── main/java/io/github/vladimirshutkov/qaa/
│   ├── api/                 # REST client and Allure HTTP filter
│   ├── config/              # UI and API configuration resolution
│   ├── models/              # Booking DTOs
│   └── ui/                  # Playwright session and Page Objects
└── test/
    ├── java/io/github/vladimirshutkov/qaa/
    │   ├── api/             # Booking scenarios and deterministic test data
    │   ├── support/         # JUnit 5 Playwright lifecycle extension
    │   └── ui/              # SauceDemo scenarios
    └── resources/
        ├── schemas/         # JSON Schema response contracts
        └── allure.properties
.github/workflows/           # CI and Allure Pages publication
docs/architecture.md         # Technical design notes
```

Tests express business intent. Page Objects encapsulate UI interaction, `BookingClient` encapsulates HTTP mechanics, and configuration/models are shared outside scenario code.

## Configuration

All settings use the same precedence: **JVM system property → environment variable → built-in default**. Blank property or environment values are ignored.

| Area | System property | Environment variable | Default |
| --- | --- | --- | --- |
| UI base URL | `qaa.ui.base-url` | `QAA_UI_BASE_URL` | `https://www.saucedemo.com` |
| UI browser | `qaa.ui.browser` | `QAA_UI_BROWSER` | `CHROMIUM` |
| UI headless mode | `qaa.ui.headless` | `QAA_UI_HEADLESS` | `true` |
| API base URL | `qaa.api.base-url` | `QAA_API_BASE_URL` | `https://restful-booker.herokuapp.com` |
| API username | `qaa.api.username` | `QAA_API_USERNAME` | `admin` |
| API password | `qaa.api.password` | `QAA_API_PASSWORD` | `password123` |

`qaa.ui.browser` accepts `CHROMIUM`, `FIREFOX`, or `WEBKIT` (case-insensitive). The API credentials above are the public Restful Booker demo defaults present in the code; use environment variables or system properties for any different environment, and never commit real credentials.

For example, `-Dqaa.ui.headless=false` overrides `QAA_UI_HEADLESS`; the environment value is used only when the system property is absent or blank.

## Running Tests

Use the Maven Wrapper so the project-selected Maven distribution is used.

Windows PowerShell:

```powershell
.\mvnw.cmd test
```

macOS/Linux:

```bash
bash ./mvnw test
```

To run UI tests in a visible browser on Windows:

```powershell
.\mvnw.cmd test '-Dqaa.ui.headless=false'
```

An individual test class can be selected through Surefire, for example:

```powershell
.\mvnw.cmd -Dtest=LoginTest test
```

## Allure Reporting

Test execution writes Allure result files to `target/allure-results`. After running tests, the configured Allure Maven plugin can serve a local HTML report:

```powershell
.\mvnw.cmd allure:serve
```

This uses the Maven plugin configured in the project; a globally installed Allure CLI is not required for that command.

CI uploads `allure-results` as an artifact, including when the test job fails. A separate GitHub Actions workflow generates the HTML report from the artifact of the exact triggering CI run and deploys it to GitHub Pages. The repository does not hard-code a Pages URL; GitHub exposes the deployed URL through the deployment environment.

## CI/CD

The `CI` workflow runs on pull requests targeting `main`, pushes to `main`, and manual dispatch. It uses Temurin JDK 21, installs Playwright Chromium, runs the Maven test suite through the wrapper, and uploads the Allure results artifact.

`Publish Allure Report` is intentionally separate from test execution. It runs only after a successful `CI` workflow run from this repository's `main` branch, triggered by a `push` or `workflow_dispatch`. It downloads the artifact using the triggering run ID, generates the report, and deploys it to GitHub Pages. These conditions keep Pages publication limited to trusted main-branch runs rather than pull-request runs.

## Design Decisions

- Page Objects keep UI selectors and interactions out of test scenarios.
- `BookingClient` centralizes REST Assured request construction, authentication, and timeouts.
- `PlaywrightExtension` creates and closes a browser/session/context per UI test.
- Web-first Playwright assertions and deterministic API payloads avoid fixed sleeps.
- API tests own cleanup of the records they create.
- Centralized configuration makes local and CI execution configurable without changing tests.
- Allure attachments provide failure diagnostics while the HTTP filter redacts configured sensitive JSON fields.
- CI invokes the Maven Wrapper rather than a runner-provided Maven installation.

## AI-Assisted Development

AI tools may be used as development assistance for review, refinement, and documentation. Changes remain subject to code review and executable test verification; AI is not treated as the source of truth.
