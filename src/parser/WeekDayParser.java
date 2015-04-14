package parser;

import java.util.Calendar;

import javax.swing.JOptionPane;

/**
 * get weekday for the date
 * 
 * @author A0112823R
 *
 */
public class WeekDayParser {

    public WeekDayParser() {

    }

    /**
     * add the weekday to the date
     * 
     * @param date : in dd/mm/yyyy format
     */
    public static String getWeekDay(String date)
            throws IllegalArgumentException {

        Calendar calendar = Calendar.getInstance();

        int day = NumberParser.getNumber(date);
        int year = YearParser.getYear(date);
        int month = MonthParser.getMonth(date);

        setDateIntoCalendar(day, month - 1, year, calendar);

        testValidDay(day, year, month);
        testValidMonth(month);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        String weekdayInWord = convertToWord(dayOfWeek);

        return weekdayInWord;
    }

    public static int detectDayOfWeek(String userInput) {
        int dayOfWeek = 0;

        if (userInput.contains("mon")) {
            dayOfWeek = 1;
        } else if (userInput.contains("tue")) {
            dayOfWeek = 2;
        } else if (userInput.contains("wed")) {
            dayOfWeek = 3;
        } else if (userInput.contains("thur")) {
            dayOfWeek = 4;
        } else if (userInput.contains("fri")) {
            dayOfWeek = 5;
        } else if (userInput.contains("sat")) {
            dayOfWeek = 6;
        } else if (userInput.contains("sun")) {
            dayOfWeek = 7;
        }

        return dayOfWeek;
    }

    /**
     * check if the date is valid if the year and the month have this day For
     * example feb only have max 29 days
     * 
     * @param day
     * @throws IllegalArgumentException
     *             : when the day of month exceeded
     */
    private static void testValidDay(int day, int year, int month)
            throws IllegalArgumentException {

        try {

            Calendar calendar = Calendar.getInstance();

            if (year != 0) {
                calendar.set(Calendar.YEAR, year);
            }

            calendar.set(Calendar.MONTH, month - 1);

            int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            if (day == 0 || maxDays < day) {
                throw new IllegalArgumentException(
                        "Invalid Day Keyed! Exceed the maximum day in that month");
            }

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * throws and catch exception of invalid month
     * 
     * @param month
     * @throws IllegalArgumentException
     *             : month > 12 or month <= 0
     */
    private static void testValidMonth(int month)
            throws IllegalArgumentException {
        try {
            if (month <= 0 || month > 12) {
                throw new IllegalArgumentException("Invalid Month Keyed!");
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * convert to word for all week day integer
     * 
     * @param dayOfWeek
     * @return
     */
    private static String convertToWord(int dayOfWeek) {
        String dayOfWeekInWord = "";

        if (dayOfWeek == 1) {
            dayOfWeekInWord = "Mon";
        } else if (dayOfWeek == 2) {
            dayOfWeekInWord = "Tues";
        } else if (dayOfWeek == 3) {
            dayOfWeekInWord = "Wed";
        } else if (dayOfWeek == 4) {
            dayOfWeekInWord = "Thur";
        } else if (dayOfWeek == 5) {
            dayOfWeekInWord = "Fri";
        } else if (dayOfWeek == 6) {
            dayOfWeekInWord = "Sat";
        } else if (dayOfWeek == 0) {
            dayOfWeekInWord = "Sun";
        }
        return dayOfWeekInWord;
    }

    private static void setDateIntoCalendar(int day, int month, int year,
            Calendar calendar) {
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
    }

}
