package net.waret.demo.photoz.web.rest.test;

import net.waret.demo.photoz.domain.Order;
import net.waret.demo.photoz.domain.Status;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
public class TestResource {

    @GetMapping(value = "/test-client-ip", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity testClientIp() {
        return ResponseEntity.status(HttpStatus.OK).body(new Order("test-client-id", Status.COMPLETED));
    }

}
