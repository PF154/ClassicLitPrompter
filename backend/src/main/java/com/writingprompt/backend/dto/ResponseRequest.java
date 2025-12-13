package com.writingprompt.backend.dto;

public class ResponseRequest {

    private String text;
    
    public ResponseRequest() {}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
