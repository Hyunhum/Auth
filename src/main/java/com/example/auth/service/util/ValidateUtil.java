package com.example.auth.service.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 정규 표현식을 활용하여 이메일, 핸드폰, 비밀번호, 이름 형식 유효성 검사
// 스태틱 메서드를 활용하여 객체 생성없이 활용하도록
public class ValidateUtil {

    private static final String EMAIL_PATTERN = "\\w+@\\w+\\.\\w+(\\.\\w+)?";
    private static final String PHONE_NUM_PATTERN = "^01(?:0|1|[6-9])[-]?(\\d{3}|\\d{4})[-]?(\\d{4}$)";
    private static final String PASSWORD_PATTERN = "^[A-Za-z0-9]{6,12}$";
    private static final String NAME_PATTERN = "^[a-zA-Z가-힣]*$";

    public static boolean validateEmail(final String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean validatePhoneNum(final String phoneNum) {
        Pattern pattern = Pattern.compile(PHONE_NUM_PATTERN);
        Matcher matcher = pattern.matcher(phoneNum);
        return matcher.matches();
    }

    public static boolean validatePassword(final String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean validateName(final String name) {
        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

}