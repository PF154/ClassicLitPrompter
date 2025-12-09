package com.writingprompt.backend.repository;

import com.writingprompt.backend.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface QuoteRepository extends JpaRepository<Quote, Long>{
    // Use buildt-in generated methods
}
