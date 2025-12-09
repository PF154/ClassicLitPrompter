package com.writingprompt.backend.repository;

import com.writingprompt.backend.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ResponseRepository extends JpaRepository<Response, Long> {
    
}
