package net.waret.demo.photoz.web.rest.album;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class TestResource {

    @GetMapping("/test-client-ip")
    public String testClientIp() {
        return "test-client-ip";
    }

}
