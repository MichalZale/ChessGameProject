package com.example.ChessProject.controller.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ApiError {
    private String code;
    private String message;
    private int status;
    private String path;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

    public ApiError() {
    }

    public ApiError(String code, String message, int status, String path) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp = Instant.now();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public String getPath() {
        return path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
