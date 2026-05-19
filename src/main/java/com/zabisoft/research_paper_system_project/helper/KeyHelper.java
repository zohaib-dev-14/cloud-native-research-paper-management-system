package com.zabisoft.research_paper_system_project.helper;

import com.zabisoft.research_paper_system_project.enums.OTPType;

public class KeyHelper {
    public static String registrationKey(
            String email
    ) {

        return "REGISTER:" + email;
    }
    public static String otpKey(
            String email,
            OTPType otpType
    ) {

        return "OTP:"
                + otpType.name()
                + ":"
                + email;
    }
    public static String resendKey(
            String email,
            OTPType otpType
    ) {

        return "RESEND:"
                + otpType.name()
                + ":"
                + email;
    }

    public static String resetAllowedKey(
            String email
    ) {
        return "RESET_ALLOWED:" + email;
    }
}
