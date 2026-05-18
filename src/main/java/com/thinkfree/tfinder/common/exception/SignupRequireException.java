package com.thinkfree.tfinder.common.exception;

public class SignupRequireException extends RuntimeException{

    public SignupRequireException() {
        super("회원가입을 먼저 완료해주세요.");
    }
}
