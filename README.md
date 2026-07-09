# CareerFlow / Owoke

[![CI](https://github.com/owokequ/CareerFlow/actions/workflows/ci.yml/badge.svg)](https://github.com/owokequ/CareerFlow/actions/workflows/ci.yml)
[![Publish Docker Image](https://github.com/owokequ/CareerFlow/actions/workflows/docker-publish.yml/badge.svg)](https://github.com/owokequ/CareerFlow/actions/workflows/docker-publish.yml)

Backend-сервис для CareerFlow: регистрация и вход пользователей, JWT-аутентификация,
refresh-сессии, подтверждение email, восстановление пароля и управление пользовательским
профилем.

Проект построен как учебно-практический backend с акцентом на безопасность,
транзакционные границы и асинхронную интеграцию между auth-слоем и user-слоем.

## Возможности

- Регистрация и логин через email/password.
- Access token через `Authorization: Bearer ...`.
- Refresh token в `HttpOnly` cookie.
- Хранение refresh token в Redis только в виде hash.
- Подтверждение email через токен.
- Восстановление пароля без user enumeration.
- Rate limit для password reset через Redis.
- Нормализация email перед сохранением и поиском.
- Создание user-профиля через Kafka event после успешного commit auth-транзакции.
- Разделение `401 Unauthorized` и `403 Forbidden`.
- Liquibase-миграции для PostgreSQL.
- GitHub Actions CI и публикация Docker image в GHCR.

## Стек

- Java 21
- Spring Boot 4.1
- Spring Security
- Spring Data JPA
- PostgreSQL
- Liquibase
- Redis
- Apache Kafka
- Maven
- Testcontainers
- Docker / Docker Compose
- GitHub Actions

## Архитектура

Основные модули:

- `auth` - регистрация, логин, JWT, refresh token, email verification, password reset.
- `user` - пользовательский профиль и admin user management.
- `messaging` - email-сервис и Kafka event contracts.
- `config` - security, Kafka, Redis и security properties.
- `common` - общие exception, util-классы и security user details.

Упрощенный поток регистрации:

```text
POST /api/auth/register
        |
        v
AuthService creates auth user in PostgreSQL
        |
        v
Spring transaction commits
        |
        v
AFTER_COMMIT listeners:
  - save hashed refresh token in Redis
  - create verification token and send email
  - publish AuthUserCreatedEvent to Kafka
        |
        v
Kafka listener creates app user profile
```

Такой подход не отправляет внешние события и письма, если database transaction
откатилась.

## Требования

- JDK 21+
- Docker Desktop
- Maven wrapper из репозитория (`mvnw` / `mvnw.cmd`)

## Локальный запуск

1. Запустить инфраструктуру:

```bash
docker compose -f docker/docker-compose.yml up -d
```

2. Запустить приложение с `local` профилем:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Для Windows:

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

Локальный профиль использует порт `8088`.

Полезные локальные адреса:

- API: `http://localhost:8088`
- Swagger UI: `http://localhost:8088/swagger-ui/index.html`
- OpenAPI docs: `http://localhost:8088/api-docs`
- Kafka UI: `http://localhost:8081`
- Redis Insight: `http://localhost:5540`
- Mailpit UI: `http://localhost:8025`
- Mailpit SMTP: `localhost:1025`
- PostgreSQL: `localhost:5433`

## Конфигурация

Основной `application.yml` не содержит production-секретов и ожидает переменные
окружения.

Обязательные переменные для production-like запуска:

| Variable | Description |
| --- | --- |
| `DB_URL` | JDBC URL PostgreSQL |
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `REDIS_HOST` | Redis host |
| `REDIS_PORT` | Redis port |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka bootstrap servers |
| `JWT_ACCESS_SECRET` | Secret для access token |
| `JWT_REFRESH_SECRET` | Secret для refresh token |

Опциональные переменные:

| Variable | Default |
| --- | --- |
| `MAIL_HOST` | `smtp.gmail.com` |
| `MAIL_PORT` | `587` |
| `MAIL_USERNAME` | empty |
| `MAIL_PASSWORD` | empty |
| `MAIL_PROTOCOL` | `smtp` |
| `MAIL_FROM` | falls back to `MAIL_USERNAME`, then `noreply@owoke.local` |
| `JWT_ACCESS_EXPIRATION` | `3600000` |
| `JWT_REFRESH_EXPIRATION` | `2592000000` |
| `JPA_SHOW_SQL` | `false` |
| `SERVER_PORT` | Spring Boot default, unless profile overrides it |

`application-local.yml` содержит безопасные dev-defaults для локальной разработки.
Пароли и dev JWT secrets из local-профиля не должны использоваться в production.

В `local` профиле email отправляется в Mailpit, а не во внешний SMTP-сервис:

```text
Spring Boot -> localhost:1025 -> Mailpit -> http://localhost:8025
```

Mailpit не требует `MAIL_USERNAME`, `MAIL_PASSWORD` и TLS. Адрес отправителя для
локальных писем задается через `MAIL_FROM`, по умолчанию используется
`noreply@owoke.local`.

## API

### Auth

| Method | Path | Access |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Public |
| `POST` | `/api/auth/login` | Public |
| `POST` | `/api/auth/refresh` | Public, refresh cookie required |
| `DELETE` | `/api/auth/logout` | Authenticated |
| `GET` | `/api/auth/register/verify?token=...` | Public |
| `POST` | `/api/auth/password/forgot` | Public |
| `POST` | `/api/auth/password/reset?token=...` | Public |

Register request:

```json
{
  "name": "Alex",
  "email": "alex@example.com",
  "password": "strong-password"
}
```

Login request:

```json
{
  "email": "alex@example.com",
  "password": "strong-password"
}
```

Forgot password request:

```json
{
  "email": "alex@example.com"
}
```

Reset password request:

```json
{
  "newPassword": "new-strong-password"
}
```

### Users

| Method | Path | Access |
| --- | --- | --- |
| `GET` | `/api/users/me` | Authenticated |
| `PUT` | `/api/users/me` | Authenticated |
| `GET` | `/api/users` | Admin |
| `GET` | `/api/users/{id}` | Admin |
| `PUT` | `/api/users/{id}` | Admin |
| `DELETE` | `/api/users/{id}` | Admin |

Update user request:

```json
{
  "name": "Alex Updated",
  "email": "alex.updated@example.com"
}
```

## Тесты

Запуск всех тестов:

```bash
./mvnw test
```

Для Windows:

```powershell
.\mvnw.cmd test
```

Интеграционные тесты используют Testcontainers, поэтому Docker должен быть запущен.

## Docker

Сборка jar:

```bash
./mvnw -B -DskipTests package
```

Сборка Docker image:

```bash
docker build -f docker/Dockerfile -t owoke .
```

Текущий Dockerfile копирует уже собранный `target/*.jar`, поэтому сначала нужен
`mvnw package`.

Если контейнер запускается без `local` профиля, явно задайте порт:

```bash
docker run --rm -p 8088:8088 -e SERVER_PORT=8088 owoke
```

## CI/CD

В проекте настроены два GitHub Actions workflow:

- `CI` - запускает `./mvnw -B test` на push и pull request в `main`.
- `Publish Docker Image` - собирает jar, собирает Docker image и публикует его в
  GitHub Container Registry.

Docker image публикуется в:

```text
ghcr.io/owokequ/CareerFlow
```

Публикация запускается на push в `main` и на tags вида `v*.*.*`.

## Security notes

- CSRF отключен, потому что access token передается через `Authorization` header.
- Refresh token хранится в `HttpOnly` cookie.
- В production refresh cookie настроена как `Secure` и `SameSite=Strict`.
- В Redis хранится hash refresh token, а не raw token.
- Password reset endpoint не раскрывает, существует ли email в системе.
- Password reset rate limit хранится в Redis по hash от normalized email.
- Kafka consumer принимает JSON events только из trusted package
  `career.flow.owoke.messaging.event`.

## Roadmap

- Добавить полноценный deployment workflow под выбранный hosting.
- Расширить покрытие integration tests для auth/user сценариев.
- Добавить health checks для внешних зависимостей.
- Настроить branch protection в GitHub.
- Добавить observability: structured logs, metrics, tracing.
