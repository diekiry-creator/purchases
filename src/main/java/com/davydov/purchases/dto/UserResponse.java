package com.davydov.purchases.dto;

import com.davydov.purchases.enums.Status;

public class UserResponse {
    private Status status;
    private String explanation;

    UserResponse(Status status) {
        this.status = status;
    }

    public static UserResponse success() {
        return new UserResponse(Status.SUCCESS);
    }

    public static UserResponse error(String explanation) {
        UserResponse response = new UserResponse(Status.FAIL);
        response.setExplanation(explanation);
        return response;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
