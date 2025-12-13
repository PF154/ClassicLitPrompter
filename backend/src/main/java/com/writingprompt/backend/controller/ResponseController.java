package com.writingprompt.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.writingprompt.backend.dto.ResponseRequest;
import com.writingprompt.backend.dto.VoteRequest;
import com.writingprompt.backend.entity.Response;
import com.writingprompt.backend.entity.VoteType;
import com.writingprompt.backend.service.ResponseService;

import org.springframework.lang.NonNull;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping()
public class ResponseController {

    private final ResponseService responseService;

    public ResponseController(ResponseService responseService) {
        this.responseService = responseService;
    }

    // Endpoints
    @PostMapping("/api/quotes/{quoteId}/responses")
    public Response submitResponse(
        @PathVariable @NonNull Long quoteId,
        @RequestBody ResponseRequest responseRequest,
        HttpServletRequest request
    ) {
        String ipAddress = request.getRemoteAddr();
        String text = responseRequest.getText();

        return responseService.submitResponse(quoteId, text, ipAddress);
    }

    @GetMapping("/api/quotes/{quoteId}/responses")
    public List<Response> getResponses(
        @PathVariable @NonNull Long quoteId,
        HttpServletRequest request
    ) {
        String ipAddress = request.getRemoteAddr();

        return responseService.getResponses(quoteId, ipAddress);
    }

    @PutMapping("/api/responses/{id}")
    public Response editResponse(
        @PathVariable @NonNull Long id,
        @RequestBody ResponseRequest responseRequest,
        HttpServletRequest request
    ) {
        String ipAddress = request.getRemoteAddr();
        String text = responseRequest.getText();

        return responseService.editResponse(id, text, ipAddress);
    }

    @DeleteMapping("/api/responses/{id}")
    public void deleteResponse(
        @PathVariable @NonNull Long id,
        HttpServletRequest request
    ) {
        String ipAddress = request.getRemoteAddr();

        responseService.deleteResponse(id, ipAddress);
    }

    @PostMapping("/api/responses/{id}/vote")
    public Response voteOnResponse(
        @PathVariable @NonNull Long id,
        @RequestBody VoteRequest voteRequest,
        HttpServletRequest request
    ) {
        String ipAddress = request.getRemoteAddr();
        VoteType voteType = voteRequest.getVoteType();

        return responseService.voteOnResponse(id, voteType, ipAddress);
    }
    
}
