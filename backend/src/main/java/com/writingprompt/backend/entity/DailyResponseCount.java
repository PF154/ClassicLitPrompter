package com.writingprompt.backend.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "daily_response_counts",
    uniqueConstraints = @UniqueConstraint(columnNames = {"ip_address", "response_date"})
)
public class DailyResponseCount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip_address", nullable = false, length=45)
    private String ipAddress;

    @Column(name = "response_date", nullable=false)
    private LocalDate responseDate;

    @Column(nullable = false)
    private Integer count = 0;

    public DailyResponseCount() {}

    public DailyResponseCount(String ipAddress)
    {
        this.ipAddress = ipAddress;
        this.responseDate = LocalDate.now();
        this.count = 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDate getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(LocalDate responseDate) {
        this.responseDate = responseDate;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
