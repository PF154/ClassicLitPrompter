package com.writingprompt.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.writingprompt.backend.entity.DailyResponseCount;
import com.writingprompt.backend.entity.Quote;
import com.writingprompt.backend.entity.Response;
import com.writingprompt.backend.entity.ResponseVote;
import com.writingprompt.backend.entity.VoteType;
import com.writingprompt.backend.repository.DailyResponseCountRepository;
import com.writingprompt.backend.repository.QuoteRepository;
import com.writingprompt.backend.repository.ResponseVoteRepository;
import com.writingprompt.backend.repository.ResponseRepository;

@Service
public class ResponseService {
    
    private final ResponseRepository responseRepository;
    private final ResponseVoteRepository responseVoteRepository;
    private final QuoteRepository quoteRepository;
    private final DailyResponseCountRepository dailyResponseCountRepository;

    public ResponseService(
        ResponseRepository responseRepository,
        ResponseVoteRepository responseVoteRepository,
        QuoteRepository quoteRepository,
        DailyResponseCountRepository dailyResponseCountRepository
    ) {
        this.responseRepository = responseRepository;
        this.responseVoteRepository = responseVoteRepository;
        this.quoteRepository = quoteRepository;
        this.dailyResponseCountRepository = dailyResponseCountRepository;
    }

    /**
     * 
     * @param quoteId Id of Quote to apply response to
     * @param text Text content of response
     * @param ipAddress IP Address of user who submitted response
     * @return New response object if it was successfully created, otherwise null
     */
    public Response submitResponse(@NonNull Long quoteId, String text, String ipAddress) {
        
        // Make sure quote actually exists
        Optional<Quote> quoteToRespondToOptional = quoteRepository.findById(quoteId);
        if (!quoteToRespondToOptional.isPresent()) return null;
        Quote quoteToRespondTo = quoteToRespondToOptional.get();

        // Check if user IP has already submitted a response
        Optional<Response> existingResponseOptional = responseRepository.findAll().stream()
            .filter(r -> r.getQuote().getId().equals(quoteId) && r.getIpAddress().equals(ipAddress))
            .findFirst();

        if (existingResponseOptional.isPresent()) {
            // Don't know what returning an error would look like
            // For now, return null
            return null;
        }

        // If the user can respond, we want to make sure they haven't exceeded their response limit for the day
        LocalDate date = LocalDate.now();

        Optional<DailyResponseCount> dailyResponseCountOptional
            = dailyResponseCountRepository.findByIpAddressAndResponseDate(ipAddress, date);

        DailyResponseCount dailyResponseCount;
        if (!dailyResponseCountOptional.isPresent()) {
            // Create the count entity if it doesn't exist yet
            dailyResponseCount = new DailyResponseCount(ipAddress);
            dailyResponseCount.setCount(0); 
        }
        else {
            dailyResponseCount = dailyResponseCountOptional.get();
        } 

        Integer count = dailyResponseCount.getCount();
        if (count >= 2) {
            // Ideally, we'd want to display a message here.
            // In liu of that, just reject the submission
            return null;
        }

        // If we're good to submit, update the response count
        dailyResponseCount.setCount(count+1);


        // Validate word count
        if (text.split("\\s+").length > 750) {
            return null;
        }

        // Create new Response
        Response newResponse = new Response(quoteToRespondTo, text, ipAddress);

        // Save and return
        responseRepository.save(newResponse);
        dailyResponseCountRepository.save(dailyResponseCount);
        return newResponse;
    }

    /**
     * 
     * @param quoteId Id of quote to query responses from
     * @param ipAddress IP Address of requester
     * @return List of responses if user can see them, null otherwise
     */
    public List<Response> getResponses(@NonNull Long quoteId, String ipAddress) {
        // Verify that user has already responded to this quote (criteria for seeing other responses)
        Optional<Quote> quoteOptional = quoteRepository.findById(quoteId);
        if (!quoteOptional.isPresent()) return null;

        Optional<Response> existingResponseOptional = responseRepository.findAll().stream()
            .filter(r -> r.getQuote().getId().equals(quoteId) && r.getIpAddress().equals(ipAddress))
            .findFirst();

        if (!existingResponseOptional.isPresent()) return null;

        // Return all the responses to this quote
        return responseRepository.findAll().stream()
            .filter(r -> r.getQuote().getId().equals(quoteId))
            .collect(Collectors.toList());
    }

    /**
     * 
     * @param id Id of response to edit
     * @param text New text to attempt to give response
     * @param ipAddress IP Address of user attempting to edit
     * @return Edited response if edit is successful, null otherwise
     */
    public Response editResponse(@NonNull Long id, String text, String ipAddress) {
        // Make sure response actually exists
        Optional<Response> responseToEditOptional = responseRepository.findById(id);
        if (!responseToEditOptional.isPresent()) return null;

        Response responseToEdit = responseToEditOptional.get();

        // Make sure that this is the user's response (can't delete other users' responses)
        if (!responseToEdit.getIpAddress().equals(ipAddress)) return null;

        // Verify text is within length limits
        if (text.split("\\s+").length > 750) return null;

        // Modify response
        responseToEdit.setText(text);
        responseToEdit.setIsEdited(true);

        // Save and return
        responseRepository.save(responseToEdit);
        return responseToEdit;
    }

    /**
     * 
     * @param id id of response to delete
     * @param ipAddress IP Address of user attempting to delete response
     */
    public void deleteResponse(@NonNull Long id, String ipAddress) {
        // Make sure response exists
        Optional<Response> responseToDeleteOptional = responseRepository.findById(id);
        if (!responseToDeleteOptional.isPresent()) return;
        Response responseToDelete = responseToDeleteOptional.get();

        // Make sure the user actually owns this response
        if (responseToDelete.getIpAddress().equals(ipAddress))
        {
            // Delete all votes associated with response
            List<ResponseVote> responseVotes = responseVoteRepository.findAll().stream()
                .filter(rv -> rv.getResponse().getId().equals(id))
                .collect(Collectors.toList());

            for (ResponseVote rv : responseVotes) {
                responseVoteRepository.delete(rv);
            }

            // Delete response itself
            responseRepository.delete(responseToDelete);
        }
    }

    /**
     * 
     * @param id id of response to vote on
     * @param voteType type of vote (UP or DOWN)
     * @param ipAddress IP Address of voter
     * @return Response that was voted on, or null if an error occured
     */
    public Response voteOnResponse(@NonNull Long id, VoteType voteType, String ipAddress) {
        // Make sure response exists
        Optional<Response> responseToVoteOnOptional = responseRepository.findById(id);
        if (!responseToVoteOnOptional.isPresent()) return null;
        Response responseToVoteOn = responseToVoteOnOptional.get();

        // Check if this user has already voted on it
        Optional<ResponseVote> existingVoteOptional = responseVoteRepository.findAll().stream()
            .filter(rv -> rv.getResponse().getId().equals(id) && rv.getIpAddress().equals(ipAddress))
            .findFirst();

        // Update Votes
        int upIncr = 0;
        int downIncr = 0;
        if (existingVoteOptional.isPresent()) {
            ResponseVote existingVote = existingVoteOptional.get();
            // User has already voted
            // Check if they changed their vote
            if (!existingVote.getVoteType().equals(voteType)) {
                existingVote.setVoteType(voteType);
                responseVoteRepository.save(existingVote);

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
            responseVoteRepository.save(new ResponseVote(ipAddress, responseToVoteOn, voteType));
        
            if (voteType == VoteType.UP) upIncr = 1;
            else downIncr = 1;
        }

        int upvotes = responseToVoteOn.getUpvotes();
        int downvotes = responseToVoteOn.getDownvotes();

        responseToVoteOn.setUpvotes(upvotes + upIncr);
        responseToVoteOn.setDownvotes(downvotes + downIncr);
        responseRepository.save(responseToVoteOn);
        
        return responseToVoteOn;
    }

}
