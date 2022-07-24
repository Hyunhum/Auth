/*package com.example.auth.service.util;

public class PhoneNumVerifyUtil {

    private boolean validatePhoneNum(String phoneNum) {
        try{
            Integer.parseInt(phoneNum.substring(1));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }    
            if (phoneNum.length() == 11
                && 10 <= Integer.parseInt(phoneNum.substring(1,3)) 
                && Integer.parseInt(phoneNum.substring(1,3)) < 20
                && phoneNum.charAt(0) == '0') {
                    return true;
        } else {
            return false;
        }
    }


}
*/