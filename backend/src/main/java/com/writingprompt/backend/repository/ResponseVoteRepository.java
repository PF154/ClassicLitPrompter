package com.writingprompt.backend.repository;

import com.writingprompt.backend.entity.ResponseVote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ResponseVoteRepository extends JpaRepository<ResponseVote, Long>{
    
}
