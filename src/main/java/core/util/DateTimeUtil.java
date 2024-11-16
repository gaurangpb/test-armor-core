package core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtil {

    public static String getCurrentDateTime(String pattern) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return now.format(formatter);
    }

    public static String getCurrentDate(String datePattern) {
        return getCurrentDate(datePattern, 0);
    }

    public static String getCurrentDate(String datePattern, int daysToAddOrSubtract) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(datePattern);
        LocalDate modifiedDate = LocalDate.now().plusDays(daysToAddOrSubtract);
        return modifiedDate.format(dtf);
    }

    public static String convertDateFormat(String inputDate, String inputFormat, String outputFormat) {
        try {
            SimpleDateFormat inputFormatter = new SimpleDateFormat(inputFormat);
            SimpleDateFormat outputFormatter = new SimpleDateFormat(outputFormat);
            Date date = inputFormatter.parse(inputDate);
            return outputFormatter.format(date);
        } catch (ParseException e) {
            return "Invalid Date Format";
        }
    }
}
