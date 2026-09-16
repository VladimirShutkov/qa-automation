---
name: api-testing
description: Использовать при работе с REST API тестами, REST Assured, API clients, моделями, JSON Schema и API test failures.
---

# API-тестирование

- HTTP-логику размещать в существующих API clients/specifications, а не в тестах.
- Переиспользовать общую конфигурацию и base URLs.
- Использовать typed request/response models, когда это улучшает читаемость.
- Проверять релевантные positive, negative, boundary и contract/schema сценарии.
- Проверять status code, значимые headers, contract и business-critical fields.
- Не проверять volatile fields без необходимости.
- Test data должны быть минимальными и детерминированными.
- При падении проверить request, response, mapping и schema до изменения кода.