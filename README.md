[![Maven Central](https://img.shields.io/maven-central/v/io.github.michaldo/sanchero-starter.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.github.michaldo/sanchero-starter)

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

For Spring Boot 2.x:

    <dependency>
        <groupId>io.github.michaldo</groupId>
        <artifactId>sanchero-starter</artifactId>
        <version>0.0.1</version>
    </dependency>

For Spring Boot 3.x:

    <dependency>
        <groupId>io.github.michaldo</groupId>
        <artifactId>sanchero-starter</artifactId>
        <version>1.0.0</version>
    </dependency>

Sanchero is compiled with Java 17.

If for any reason the library must be customized, I recommend copy-paste source code. It is just one file.

> WARNING: Sanchero reports ConstraintViolationException with 400 code, 
> regardless throw in `Conroller` layer or `Service` layer. Exceptions from `Service` layer should be reported
> with code 500.
> 
> It is not clear to me how to fix that. Fortunately, often ConstraintViolationException is thrown only from
> `Conroller` layer.

