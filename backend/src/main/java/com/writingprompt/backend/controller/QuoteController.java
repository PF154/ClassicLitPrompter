package com.writingprompt.backend.controller;

import org.springframework.web.bind.annotation.*;

import com.writingprompt.backend.entity.Quote;
import com.writingprompt.backend.service.QuoteService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/quotes")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    // Endpoint for random quote pulling
    @GetMapping("/random")
    public Quote getRandomQuote(HttpServletRequest request) {
        String ipString = request.getRemoteAddr();
        return quoteService.getRandomQuote(ipString);
    }

}
