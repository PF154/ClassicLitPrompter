package com.writingprompt.backend.dto;

import com.writingprompt.backend.entity.VoteType;

public class VoteRequest {

    private VoteType voteType;  // UP or DOWN

    public VoteRequest() {}

    public VoteRequest(VoteType voteType) {
        this.voteType = voteType;
    }

    public VoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteType voteType) {
        this.voteType = voteType;
    }
}
