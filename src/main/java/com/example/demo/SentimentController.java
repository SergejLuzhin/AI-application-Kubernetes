package com.example.demo;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SentimentController {

    private final SentimentService service;
    private final MeterRegistry registry;

    public SentimentController(SentimentService service, MeterRegistry registry) {
        this.service = service;
        this.registry = registry;
    }

    @GetMapping("/sentiment")
    public Map<String, Object> sentiment(@RequestParam(defaultValue = "") String text) {
        Map<String, Object> result = service.analyze(text);

        registry.counter("sentiment_requests_total", "sentiment",
                String.valueOf(result.get("sentiment"))).increment();
        return result;
    }
}