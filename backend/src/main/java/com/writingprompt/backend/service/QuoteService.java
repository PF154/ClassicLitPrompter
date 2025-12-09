package com.writingprompt.backend.service;

import com.writingprompt.backend.entity.Quote;
import com.writingprompt.backend.entity.QuoteView;
import com.writingprompt.backend.entity.QuoteVote;
import com.writingprompt.backend.entity.VoteType;
import com.writingprompt.backend.repository.QuoteRepository;
import com.writingprompt.backend.repository.QuoteViewRepository;
import com.writingprompt.backend.repository.QuoteVoteRepository;

import java.util.*;
import java.util.stream.*;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final QuoteViewRepository quoteViewRepository;
    private final QuoteVoteRepository quoteVoteRepository;

    public QuoteService(
        QuoteRepository quoteRepository, 
        QuoteViewRepository quoteViewRepository,
        QuoteVoteRepository quoteVoteRepository
    ) {
        this.quoteRepository = quoteRepository;
        this.quoteViewRepository = quoteViewRepository;
        this.quoteVoteRepository = quoteVoteRepository;
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

    public Quote voteOnQuote(@NonNull Long id, VoteType voteType, String ipAddress) {
        Optional<Quote> quoteToVoteOnOptional = quoteRepository.findById(id);
        if (!quoteToVoteOnOptional.isPresent()) return null;
        Quote quoteToVoteOn = quoteToVoteOnOptional.get();

        Optional<QuoteVote> existingVoteOptional = quoteVoteRepository.findAll().stream()
            .filter(qv -> qv.getQuote().getId().equals(id) && qv.getIpAddress().equals(ipAddress))
            .findFirst();

        int upIncr = 0;
        int downIncr = 0;
        if (existingVoteOptional.isPresent()) {
            QuoteVote existingVote = existingVoteOptional.get();
            // User has already voted
            // Check if they changed their vote
            if (!existingVote.getVoteType().equals(voteType)) {
                existingVote.setVoteType(voteType);
                quoteVoteRepository.save(existingVote);

                if (voteType == VoteType.UP) {
                    upIncr = 1;
                    downIncr = -1;
                }
                else {
                    upIncr = -1;
                    downIncr = 1;
                }
            }
        }
        else {
            // Save new vote
            quoteVoteRepository.save(new QuoteVote(ipAddress, quoteToVoteOn, voteType));
        
            if (voteType == VoteType.UP) upIncr = 1;
            else downIncr = 1;
        }

        int upvotes = quoteToVoteOn.getUpvotes();
        int downvotes = quoteToVoteOn.getDownvotes();

        quoteToVoteOn.setUpvotes(upvotes + upIncr);
        quoteToVoteOn.setDownvotes(downvotes + downIncr);
        quoteRepository.save(quoteToVoteOn);
        
        return quoteToVoteOn;
    }
}
