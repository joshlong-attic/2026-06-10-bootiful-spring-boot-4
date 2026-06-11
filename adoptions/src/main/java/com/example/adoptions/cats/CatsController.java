package com.example.adoptions.cats;

import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.atomic.AtomicInteger;

@Controller
@ResponseBody
class CatsController {

    private final CatFactsClient catFactsClient;

    private final AtomicInteger counter = new AtomicInteger(0);

    CatsController(CatFactsClient catFactsClient) {
        this.catFactsClient = catFactsClient;
    }

    @ConcurrencyLimit(10)
    @Retryable(maxRetries = 5, includes = IllegalStateException.class)
    @GetMapping("/cats")
    CatFacts facts() {
        if (this.counter.incrementAndGet() < 5) {
            IO.println("ooops!");
            throw new IllegalStateException("ooops!");
        }

        IO.println("yay!");

        return this.catFactsClient.facts();
    }

}
