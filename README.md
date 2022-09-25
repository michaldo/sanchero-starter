# Sanchero
## Sancho Pansa error library

Sanchero is single file library for Spring Boot Web, which solves 2 well known problems:

1. ConstraintViolationException is reported with code 500 when HTTP request fails `Controller` validation
2. Detail flood when POST request body fails validation

Example 1. `curl localhost:8080/cve?p1=-1\&p2=-1`
### Vanilla Spring Boot
```json
{
  "timestamp": "2022-09-19T22:09:25.242+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "exception": "javax.validation.ConstraintViolationException",
  "message": "cve.p1: must be greater than or equal to 0, cve.p2: must be greater than or equal to 0",
  "path": "/cve"
}
```
### Sanchero
```json
{
  "timestamp": "2022-09-19T22:14:05.060+00:00",
  "status": 400,
  "error": "Bad Request",
  "exception": "javax.validation.ConstraintViolationException",
  "message": "Validation failed",
  "path": "/cve",
  "errors": [
    {
      "field": "p1",
      "description": "must be greater than or equal to 0"
    },
    {
      "field": "p2",
      "description": "must be greater than or equal to 0"
    }
  ]
}
```

Example 2. `curl -v localhost:8080/manve -d '{ "p1": -1, "p2": -1}' -H "Content-Type: application/json"`
### Vanilla Spring Boot
```json
{
  "timestamp": "2022-09-19T22:10:36.114+00:00",
  "status": 400,
  "error": "Bad Request",
  "exception": "org.springframework.web.bind.MethodArgumentNotValidException",
  "message": "Validation failed for object='rec'. Error count: 2",
  "errors": [
    {
      "codes": [
        "Min.rec.p1",
        "Min.p1",
        "Min.java.lang.Integer",
        "Min"
      ],
      "arguments": [
        {
          "codes": [
            "rec.p1",
            "p1"
          ],
          "arguments": null,
          "defaultMessage": "p1",
          "code": "p1"
        },
        0
      ],
      "defaultMessage": "must be greater than or equal to 0",
      "objectName": "rec",
      "field": "p1",
      "rejectedValue": -1,
      "bindingFailure": false,
      "code": "Min"
    },
    {
      "codes": [
        "Min.rec.p2",
        "Min.p2",
        "Min.java.lang.Integer",
        "Min"
      ],
      "arguments": [
        {
          "codes": [
            "rec.p2",
            "p2"
          ],
          "arguments": null,
          "defaultMessage": "p2",
          "code": "p2"
        },
        0
      ],
      "defaultMessage": "must be greater than or equal to 0",
      "objectName": "rec",
      "field": "p2",
      "rejectedValue": -1,
      "bindingFailure": false,
      "code": "Min"
    }
  ],
  "path": "/manve"
}
```
### Sanchero
```json
{
  "timestamp": "2022-09-19T22:12:44.470+00:00",
  "status": 400,
  "error": "Bad Request",
  "exception": "org.springframework.web.bind.MethodArgumentNotValidException",
  "message": "Validation failed",
  "errors": [
    {
      "field": "p1",
      "description": "must be greater than or equal to 0"
    },
    {
      "field": "p2",
      "description": "must be greater than or equal to 0"
    }
  ],
  "path": "/manve"
}
```

### Installation
    <dependency>
        <groupId>io.github.michaldo</groupId>
        <artifactId>sanchero-starter</artifactId>
        <version>0.0.1</version>
    </dependency>

Sanchero is compiled with dependency to Spring Boot 2.7.3 and with Java 17.

If for any reason the library must be customized, I recommend copy-paste source code. It is just one file.

> WARNING: Sanchero reports ConstraintViolationException with 400 code, 
> regardless throw in `Conroller` layer or `Service` layer. Exceptions from `Service` layer should be reported
> with code 500.
> 
> It is not clear to me how to fix that. Fortunately, often ConstraintViolationException is thrown only from
> `Conroller` layer.

### Sanchero vs RFC 7807 "Problem details for HTTP APIs"

RFC 7807 https://www.rfc-editor.org/rfc/rfc7807 defines standard error format. 
The standard is published in 2016 and not popular so far.

Sanchero follows Spring Boot error format.

### Sanchero vs Zalando Problem library

zalando/problem https://github.com/zalando/problem is RFC 7807 implementation.

zalando/problem Spring implementation is built over `@ControllerAdvice` and has the following drawbacks:
1. Does not handle exceptions thrown in Filter
2. Does not handle `HttpServletResponse.sendError(...)`
3. Modifies security configuration:

> For `http.authorizeRequests().anyRequest().authenticated().and().build();` response code
is 403 for vanilla Spring Boot and 401 for Spring Boot + problem-spring-web-starter v. 0.27.0

Sanchero is built over combination `@ControllerAdvice`/`ErrorAttributes` and not affected by these drawbacks 
