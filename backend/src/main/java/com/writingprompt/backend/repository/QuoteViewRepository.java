package com.writingprompt.backend.repository;

import com.writingprompt.backend.entity.QuoteView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface QuoteViewRepository extends JpaRepository<QuoteView, Long>{
    
}
