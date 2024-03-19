package com.plac.domain.destination.controller;

import com.plac.domain.destination.service.DestinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/destination")
public class DestinationController {

    private final DestinationService destinationService;

    @GetMapping("/top7")
    public ResponseEntity<?> getTop7Destinations(@RequestParam("filter") int filter) {
        List<String> result = destinationService.getTop7SearchWords(filter);
        return ResponseEntity.ok().body(result);
    }
}
