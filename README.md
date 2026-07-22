# plusminus-security

Security implementation for a java/spring web application.

Plusminus Security is a modular, token-based security stack for Spring Boot applications.
Instead of configuring Spring Security, you add the modules you need and get authentication,
authorization, JWT tokens, login/logout endpoints and user management out of the box via
Spring Boot auto-configuration (`spring.factories`).

## Modules

| Module | Description |
|---|---|
| `plusminus-security-core` | Core model and SPI: the `Security` object (username, roles, parameters) and the `SecurityService` that orchestrates pluggable `TokenContext`, `TokenProcessor`, `CredentialService` and `SecurityParameterProvider` implementations. |
| `plusminus-authentication` | Request authentication: `HttpTokenContext` reads the token from the `Authorization` header or the `AUTH-TOKEN` cookie, with `@Public` annotation and URI-based checkers to mark endpoints that skip authentication. |
| `plusminus-authorization` | Annotation-driven authorization: `@Role`, `@RolesAllowed` and `@LocalhostOnly` are enforced on controller methods by `Authorizer` implementations invoked from a request-scoped listener. |
| `plusminus-jwt` | JWT implementation of `TokenProcessor` (`JwtProcessor` with generator/parser) built on Nimbus JOSE + JWT and BouncyCastle RSA keys. |
| `plusminus-login` | `POST /login` (HTML and JSON variants) and `POST /logout` controllers; the login view template is configurable via `plusminus.login.template`. |
| `plusminus-user` | `User` JPA entity (roles, status, multi-tenancy support) with `UserRepository`, BCrypt-based `UserService` and a `CredentialService` implementation that authenticates against stored users. |
| `plusminus-security` | Umbrella module that pulls in all of the above — add this single dependency to get the full stack. |

## Usage

For the complete stack, add the umbrella artifact:

```xml
<dependency>
    <groupId>software.plusminus</groupId>
    <artifactId>plusminus-security</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Alternatively, depend on individual modules (same `groupId` and `version`) to pick only the
pieces you need — for example `plusminus-authentication` plus `plusminus-jwt` for stateless
token authentication without login pages or user persistence.

All modules register their configuration through Spring Boot auto-configuration, so no
additional `@Import` or component scanning is required.

## Building

The project targets JDK 8 and builds with the Maven wrapper:

```bash
./mvnw clean install
```

Code quality checks (Checkstyle, PMD, SpotBugs) and JaCoCo test coverage are enforced
during the build via the shared `plusminus-parent` pom.

## License

Licensed under the [Apache License, Version 2.0](LICENSE).
