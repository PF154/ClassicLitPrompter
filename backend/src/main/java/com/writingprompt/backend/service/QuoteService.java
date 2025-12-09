package com.writingprompt.backend.service;

import com.writingprompt.backend.entity.Quote;
import com.writingprompt.backend.entity.QuoteView;
import com.writingprompt.backend.repository.QuoteRepository;
import com.writingprompt.backend.repository.QuoteViewRepository;

import java.util.*;
import java.util.stream.*;

import org.springframework.stereotype.Service;

@Service
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final QuoteViewRepository quoteViewRepository;

    public QuoteService(QuoteRepository quoteRepository, QuoteViewRepository quoteViewRepository) {
        this.quoteRepository = quoteRepository;
        this.quoteViewRepository = quoteViewRepository;
    }

    public Quote getRandomQuote(String ipString) {
        List<Quote> allQuotes = quoteRepository.findAll();
        List<QuoteView> allQuoteViews = quoteViewRepository.findAll();
        
        // Get the quotes this user has already seen (by IP)
        Set<Long> viewedQuoteIds = allQuoteViews.stream()
            .filter(view -> view.getIpAddress().equals(ipString))
            .map(view -> view.getQuote().getId())
            .collect(Collectors.toSet());

        List<Quote> unviewedQuotes = allQuotes.stream()
            .filter(quote -> !viewedQuoteIds.contains(quote.getId()))
            .collect(Collectors.toList());

        // If we've seen everything, just get a random quote
        // No need to update views here, since we've already viewed it by definition
        if (unviewedQuotes.isEmpty()) {
            Quote randomQuote = allQuotes.get(new Random().nextInt(allQuotes.size()));
            return randomQuote;
        }

        // Otherwise, get quote by weighted sum of upvotes
        int totalWeight = 0;
        for (Quote quote : unviewedQuotes) {
            int weight = Math.max(1, quote.getUpvotes() - quote.getDownvotes() + 1);
            totalWeight += weight;
        }

        Random random = new Random();
        int randomValue = random.nextInt(totalWeight);

        int cumulativeWeight = 0;
        Quote selectedQuote = null;

        for (Quote quote : unviewedQuotes) {
            int weight = Math.max(1, quote.getUpvotes() - quote.getDownvotes() + 1);
            cumulativeWeight += weight;

            if (randomValue < cumulativeWeight) {
                selectedQuote = quote;
                break;
            }
        }

        quoteViewRepository.save(new QuoteView(ipString, selectedQuote));
        return selectedQuote;
    }

}
