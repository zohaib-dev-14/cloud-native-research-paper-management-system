package com.zabisoft.research_paper_system_project.util;

import java.util.Random;

public class OTPGeneration {
    public static String generateOTP() {
        Random random = new Random();
        // 6 digits + value
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
