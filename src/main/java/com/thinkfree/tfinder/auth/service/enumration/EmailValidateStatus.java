package com.thinkfree.tfinder.auth.service.enumration;

public enum EmailValidateStatus {

    PENDING("pending"), VALIDATE("validate");

    private final String status;
    EmailValidateStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
