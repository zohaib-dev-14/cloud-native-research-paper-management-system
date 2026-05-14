package com.zabisoft.research_paper_system_project.helper;

public class KeyHelper {
    public static String otpKey(String email) {
        return "OTP:" + email;
    }

    public static String resendKey(String email) {
        return "RESEND:" + email;
    }

    public static String verifiedKey(String email) {
        return "VERIFIED:" + email;
    }
    public static String registerationKey(String email) {
        return "REGISTER:" + email;
    }
}
