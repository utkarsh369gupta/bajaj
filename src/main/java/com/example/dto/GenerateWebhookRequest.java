package com.example.dto;

public record GenerateWebhookRequest(
        String name,
        String regNo,
        String email
) {}
