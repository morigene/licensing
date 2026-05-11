# Bank Licensing Portal API Documentation

## Overview

This document provides the REST API specification for the Bank Licensing Portal.

The API enables applicants and regulators to:

- Create a new bank license application
- Retrieve application details
- Transition an application through the approval workflow
- Approve or reject an application


**Base URL**

```http
http://localhost:8080/api/v1/applications
```

---

## Application Workflow

| Current State | Action | Next State |
|--------------|--------|-----------|
| Start Draft | Save | Draft Saved |
| Draft Saved | Edit and Save | Draft Saved |
| Draft Saved | Edit and Submit | Submitted |
| Start Draft | Submit | Submitted |
| Submitted | Request Information | Information Requested |
| Submitted | Compliance Check | Reviewed Compliance Check |
| Information Requested | Resubmit | Resubmitted |
| Resubmitted | Review Complete | Reviewed Compliance Check |
| Reviewed Compliance Check | Approve | Approved |
| Reviewed Compliance Check | Reject | Rejected |

---
## Technology Stack

The Bank Licensing Portal API is developed using the following technologies and libraries:

### Core Technologies

- **Java 17**
- **Spring Boot 4.0.6**
- **Apache Maven**
- **Embedded Apache Tomcat**

### Spring Boot Starters

- **spring-boot-starter-web** – Builds RESTful APIs.
- **spring-boot-starter-validation** – Supports request validation using Jakarta Bean Validation.
- **spring-boot-starter-security** – Provides authentication and authorization features.
- **spring-boot-starter-data-jpa** – Enables database persistence using JPA and Hibernate.
- **spring-boot-starter-test** – Supports unit and integration testing.

### Security Libraries

- **JJWT 0.11.5**
    - `jjwt-api`
    - `jjwt-impl`
    - `jjwt-jackson`

Used for generating and validating JSON Web Tokens (JWT).



### Object Mapping

- **MapStruct 1.5.5.Final**
- **mapstruct-processor 1.5.5.Final**

Used to map between Entities, DTOs, and Response objects.


### Database Support

The application uses Spring Data JPA and can be connected to databases such as:

- Oracle Database
- PostgreSQL
- MySQL

### Build Tool

- **Apache Maven**

Used for dependency management, building, testing, and packaging the application as an executable JAR file.

# 1. Create Application

Creates a new bank licensing application.

## Endpoint

```http
POST /api/v1/applications
```

## Request Headers

| Header | Value |
|------|------|
| Content-Type | application/json |
| Authorization | Bearer {token} |

## Request Payload

```json
{
  "applicantName": "ABC Bank Ltd",
  "licenseType": "Commercial Bank",
  "country": "Rwanda",
  "represententiveName":"repr xxx",
  "representativeId": "rep_id....",
  "representativePhone":"+2507....",
  "tin": "xxx",
  "contactEmail": "info@abcbank.com"
}
```

## Success Response — 201 Created

```json
{
  "applicationId": "550e8400-e29b-41d4-a716-446655440000",
  "applicantName": "ABC Bank Ltd",
  "licenseType": "Commercial Bank",
  "currentState": "Start Draft",
  "createdAt": "2026-05-11T09:00:00Z"
}
```

## Possible Responses

| HTTP Code | Description |
|---------|---------|
| 201 | Application successfully created |
| 400 | Invalid request payload |
| 401 | Unauthorized |
| 403 | Forbidden |
| 500 | Internal Server Error |

---

# 2. Get Application Details

Retrieves application information by ID.

## Endpoint

```http
GET /api/v1/applications/{applicationId}
```

## Example Request

```http
GET /api/v1/applications/550e8400-e29b-41d4-a716-446655440000
```

## Success Response — 200 OK

```json
{
  "applicationId": "123e4567-e89b-12d3-a456-426614174000",
  "applicantName": "ABC Bank Ltd",
  "licenseType": "Commercial Bank",
  "currentState": "Submitted",
  "lastUpdated": "2026-05-11T10:15:00Z"
}
```

## Possible Responses

| HTTP Code | Description |
|---------|---------|
| 200 | Request successful |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Application not found |
| 500 | Internal Server Error |

---

# 3. Transition Application State

Moves an application from one state to another.

## Endpoint

```http
POST /api/v1/applications/{applicationId}/transition
```

## Request Payload

```json
{
  "action": "Submit",
  "comment": "Application completed and submitted for review."
}
```

## Success Response — 200 OK

```json
{
  "applicationId": "550e8400-e29b-41d4-a716-446655440000",
  "previousState": "Start Draft",
  "action": "Submit",
  "currentState": "Submitted",
  "timestamp": "2026-05-11T10:30:00Z"
}
```

## Possible Responses

| HTTP Code | Description |
|---------|---------|
| 200 | Transition successful |
| 400 | Invalid request payload |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Application not found |
| 409 | Invalid state transition |
| 500 | Internal Server Error |

---

# 4. Standard Error Response

```json
{
  "timestamp": "2026-05-11T10:35:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Action 'Approve' is not allowed from state 'Draft Saved'.",
  "path": "/api/v1/applications/BLP-1001/transition"
}
```

---

# 5. Build and Package as Executable JAR

The application is packaged as a standalone executable JAR containing embedded Apache Tomcat.

## Build Command

```bash
mvn clean package
```

## Output Artifact

```text
target/bank-licensing-api-1.0.0.jar
```

---

# 6. Run the Application

```bash
java -jar target/bank-licensing-api-1.0.0.jar
```

## Expected Startup Log

```text
Tomcat started on port(s): 8080 (http)
Started BankLicensingApplication
```

---

# 7. Production Startup Example

```bash
java -Xms512m -Xmx1024m -jar bank-licensing-api-1.0.0.jar
```

---

# 8. Testing with Postman

The API should be tested using Postman after deployment.

## Create Application

- Method: POST
- URL: `http://localhost:8080/api/v1/applications`

```json
{
  "applicantName": "ABC Bank Ltd",
  "licenseType": "Commercial Bank",
  "countryOfIncorporation": "Rwanda",
  "contactEmail": "info@abcbank.com"
}
```

## Get Application

- Method: GET
- URL: `http://localhost:8080/api/v1/applications/BLP-1001`

## Submit Application

- Method: POST
- URL: `http://localhost:8080/api/v1/applications/BLP-1001/transition`

```json
{
  "action": "Submit",
  "comment": "Submitting application"
}
```

---

# 9. cURL Examples

## Create Application

```bash
curl -X POST http://localhost:8080/api/v1/applications   -H "Content-Type: application/json"   -d '{
    "applicantName":"ABC Bank Ltd",
    "licenseType":"Commercial Bank",
    "countryOfIncorporation":"Rwanda",
    "contactEmail":"info@abcbank.com"
  }'
```

## Get Application

```bash
curl http://localhost:8080/api/v1/applications/BLP-1001
```

## Submit Application

```bash
curl -X POST http://localhost:8080/api/v1/applications/BLP-1001/transition   -H "Content-Type: application/json"   -d '{"action":"Submit"}'
```

---

# 10. Assumptions and Constraints

- Authentication is handled externally.
- Authorization is role-based.
- All timestamps use UTC in ISO 8601 format.
- Application IDs are system-generated.
- State transitions are validated by business rules.

---

# 11. Recommended Future Enhancements

Due to time constraints, my focus
was on design document and coding part is not completed
the following features were not implemented in the current version and are recommended if you extend time or allow me to explain:

- Input validation
- Centralized exception handling
- Audit logging
- Unit and integration testing
- CI/CD pipeline
- Monitoring and health checks

---

# 12. Swagger/OpenAPI Support

## Maven Dependency

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

## Swagger UI URL

```http
http://localhost:8080/swagger-ui.html
```

---

# 13. Summary

| Step | Command |
|------|------|
| Build | `mvn clean package` |
| Run | `java -jar target/bank-licensing-api-1.0.0.jar` |
| Test | Postman |
| Base URL | `http://localhost:8080/api/v1/applications` |
| Embedded Server | Apache Tomcat |

---

# 14. Conclusion

The Bank Licensing Portal API is packaged as a standalone executable JAR with embedded Apache Tomcat.

The API can be started using a single command and tested immediately using Postman.

This document includes sample request payloads, sample responses, HTTP status codes, and deployment instructions to support development, testing, and integration.

#### N.B: Attached SRS document for this app and I focused on that rather actual finished built code. It's inside this repository

#   l i c e n s i n g 
 
 
