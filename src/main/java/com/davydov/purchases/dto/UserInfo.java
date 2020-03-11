package com.davydov.purchases.dto;

import javax.validation.constraints.NotNull;

public class UserInfo {
    @NotNull
    private Long userId;
    @NotNull
    private String explanation;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
