package com.zabisoft.research_paper_system_project.util;

import java.time.LocalDateTime;
import java.util.Date;

public class DateUtil {
    public static Date expirationDateTime() {
return new Date(System.currentTimeMillis() + 1000 * 60 * 15);
    }

    public static Date currentDate() {
        return new Date(System.currentTimeMillis());
    }

    public static Date expiryDateForSevenDays() {
        return new Date(System.currentTimeMillis() +
                1000L * 60 * 60 * 24 * 7);
    }
}
