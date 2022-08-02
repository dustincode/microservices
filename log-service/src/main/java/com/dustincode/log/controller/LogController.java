package com.dustincode.log.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    @GetMapping
    public ResponseEntity<?> getLogs() {
        return ResponseEntity.ok().build();
    }
}
