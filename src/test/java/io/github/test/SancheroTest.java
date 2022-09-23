package io.github.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SancheroTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCve() {

        ResponseEntity<String> response = restTemplate.getForEntity("/cve?p1=-1&p2=-1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThatJson(response.getBody())
                .isEqualTo("""
                        {
                          "timestamp": "${json-unit.ignore}",
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
                        """);
    }

    @Test
    void testManve() {

        ResponseEntity<String> response = restTemplate.postForEntity("/manve", Map.of("p1", "-1", "p2", "-1"), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThatJson(response.getBody())
                .isEqualTo("""
                        {
                          "timestamp": "${json-unit.ignore}",
                          "status": 400,
                          "error": "Bad Request",
                          "exception": "org.springframework.web.bind.MethodArgumentNotValidException",
                          "message": "Validation failed",
                          "path": "/manve",
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
                        """);
    }

}
