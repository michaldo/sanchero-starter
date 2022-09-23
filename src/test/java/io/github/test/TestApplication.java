package io.github.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@SpringBootApplication
@RestController
@Validated
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @GetMapping("/cve")
    void cve(@RequestParam @Min(0) Integer p1, @RequestParam @Min(0) Integer p2) {

    }

    @PostMapping("/manve")
    void manve(@Valid @RequestBody Rec rec){

    }

    record Rec(@Min(0) Integer p1, @Min(0) Integer p2) {}
}
