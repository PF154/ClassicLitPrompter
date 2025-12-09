package com.writingprompt.backend.repository;

import com.writingprompt.backend.entity.QuoteVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface QuoteVoteRepository extends JpaRepository<QuoteVote, Long>{
    
}
