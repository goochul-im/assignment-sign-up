package com.thinkfree.tfinder.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationMessage {

    public static final String NOT_BLANK_MESSAGE = "비어있거나 공백은 허용되지 않습니다.";
    public static final String EMAIL_MESSAGE = "이메일 형식이 올바르지 않습니다.";
    public static final String USERNAME_PATTERN_MESSAGE = "한글, 영어, 숫자의 조합만 허용됩니다";


}
