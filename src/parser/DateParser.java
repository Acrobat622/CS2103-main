package parser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

/**
 * 1) dd/mm/yyyy 2) dd/mm 3) dd month in word/shortform month yyyy 4) dd month
 * in word/shortform month 5) _ day/week/month/year later 6) the following
 * day/tmr/after today/after tomorrow/fortnight 6) on/by/due/due
 * on/from/to/next/this weekday
 * 
 * @author A0112823R
 *
 */
public class DateParser {

    private final String DDMMYYYY_KEYWORD = "\\b\\d+([/.-]\\d+[/.-]\\d+|[/.-]\\d+\\b)\\b";
    private final String DD_MONNTHINWORD_YYYY_KEYWORD = "\\b(\\w+|)(-|)\\w+(th|nd|rd|st|)(\\s|\\S)(of |)(january\\b|febuary\\b|march\\b|april\\b|may\\b|june\\b|july\\b|august\\b"
            + "|september\\b|octobor\\b|november\\b|december\\b)(\\s|)((in (the|) (year|yr)(s|))|)(\\s|)(\\d+\\b|)";
    private final String DD_SHORTFORMMONTHINWORD_YYYY_KEYWORD = "\\b(\\w+|)(-|)\\w+(th|nd|rd|st|)(\\s|\\S)(of |)(jan\\b|feb\\b|mar\\b|apr\\b|may\\b|jun\\b|jul\\b|aug\\b"
            + "|sep\\b|oct\\b|nov\\b|dec\\b)(\\s|\\S)((in (the|) (year|yr)(s|))|)(\\s|)(\\d+\\b|)";
    private final String AFTER_DAYS_APART_KEYWORD = "((\\w+|\\w+-\\w+) day(s|) later)\\b|\\bafter (\\w+|\\w+-\\w+) day(s|)\\b|\\b(\\w+|\\w+-\\w+) day(s|) after\\b";
    private final String DAYS_APART_VOCAB_KEYWORD = "(\\btmr\\b|\\b(the\\s|)following day(s|)\\b|\\b(after today)"
            + "\\b|\\b(after (tomorrow|tmr)\\b)|(\\bfortnight\\b)\\b)";
    private final String THIS_WEEKDAY_APART_KEYWORD = "\\b(on|by|due on|due|from|to|at|@|this)( this|)\\s(mon|tues|wed|thurs|fri|sat|sun)(day|nesday|urday|)(s|)\\b";
    private final String NEXT_WEEKDAY_APART_KEYWORD = "\\b(on|by|due on|due|from|to|at|@|next)( next|) (mon|tues|wed|thurs|fri|sat|sun)(day|nesday|urday|)(s|)\\b";
    private final String WEEKS_MONTHS_YEARS_APART_KEYWORD = "\\b((\\w+|\\w+-\\w+) (week|wk|month|mth|year|yr)(s|) later\\b)|\\bafter (\\w+|\\w+-\\w+) (week|wk|month|mth|year|yr)(s|)\\b|"
            + "\\b(\\w+|\\w+-\\w+) (week|wk|month|mth|year|yr)(s|) after\\b";
    private final String THIS_WEEK_MONTH_YEAR_KEYWORD = "\\bthis (week|wk)(s|)\\b|\\bthis (month|mth)(s|)\\b|\\bthis (year|yr)(s|)\\b";
    private final String TO_BE_REMOVED_KEYWORD = "\\b(@ |due on |on |at |from |to |by |due |next |this |((in (the|) (year|yr)(s|))))\\b";
    private final String DATE_FORMAT = "dd/MM/yyyy";
    private final int WEEK_UNIT = 7;
    private final int FORTNIGHT_UNIT = 14;
    private String inputLeft;
    private int index;
    private ArrayList<String> storageOfDate = new ArrayList<String>();

    public DateParser() {

    }

    public DateParser(String userInput) throws IllegalArgumentException {
        extractDate(userInput, userInput);
    }

    public void extractDate(String userInput, String leftOverInput)
            throws IllegalArgumentException {
        userInput = switchAllToLowerCase(userInput);
        leftOverInput = switchAllToLowerCase(leftOverInput);
        inputLeft = leftOverInput;
        goThroughDetectionMethod(userInput);
    }

    /**
     * to prevent case sensitive, switch all to lower case
     * 
     * @param userInput
     * @return the user input all in lower case.
     */
    private String switchAllToLowerCase(String userInput) {
        userInput = userInput.replaceAll("\\s+", " ");
        userInput = " " + userInput.toLowerCase() + " ";
        return userInput;
    }

    /**
     * get the input left after removing all the date detected
     * 
     * @return the input left after removing all the date detected
     */
    public String getInputLeft() {
        return inputLeft;
    }

    /**
     * 
     * @return String in the format of dd/mm/yyyy and return the current date if
     *         nothing is detected
     */
    public ArrayList<String> getDateList() {
        return storageOfDate;
    }

    /**
     * get the last index of the position of date detected in user input
     * 
     * @return last index
     */
    public int getIndex() {
        return index;
    }

    private void goThroughDetectionMethod(String userInput)
            throws IllegalArgumentException {

        spotDDMonthInWordYYYY(userInput, DD_MONNTHINWORD_YYYY_KEYWORD);
        spotDDMonthInWordYYYY(userInput, DD_SHORTFORMMONTHINWORD_YYYY_KEYWORD);
        spotDDMMYYYYKeyword(userInput);
        spotAfterDaysApartKeyword(userInput);
        spotDaysApartVocab(userInput);
        spotWeekMonthYearApartKeyword(userInput);
        spotThisWeekMonthYearKeyword(userInput);
        spotThisWeekdayApartKeyWord(userInput);
        spotNextWeekdayApartKeyWord(userInput);

    }

    /**
     * spot this week, month, year means date of end of this week, end of this
     * month and end of this year
     * 
     * @param userInput
     */
    private void spotThisWeekMonthYearKeyword(String userInput) {
        String dateOfTheTask = "";
        Pattern dateDetector = Pattern.compile(THIS_WEEK_MONTH_YEAR_KEYWORD);
        Matcher containDate = dateDetector.matcher(inputLeft);
        Matcher toGetIndex = dateDetector.matcher(userInput);

        while (containDate.find() && toGetIndex.find()) {
            String date = containDate.group();
            inputLeft = inputLeft.replaceAll(date, "");

            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);

            if (date.contains("week") || date.contains("wk")) {
                dateOfTheTask = getDateForThisWeek(calendar);

            } else if (date.contains("month") || date.contains("mth")) {
                dateOfTheTask = getDateForThisMonth(calendar, month, year);

            } else if (date.contains("year") || date.contains("yr")) {
                dateOfTheTask = getDateForThisYear(calendar, year);
            }

            storageOfDate.add(dateOfTheTask);
            int indexMatched = toGetIndex.start();
            setThePosition(indexMatched);
        }
    }

    private String getDateForThisWeek(Calendar calendar) {
        int todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        String dateOfTheTask = getThisWeekayDate(WEEK_UNIT, todayDayOfWeek);
        return dateOfTheTask;
    }

    private String getDateForThisYear(Calendar calendar, int year) {
        // set the month to dec (11 is dec month num)
        calendar.set(Calendar.MONTH, 11);

        int maxDaysOfDec = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        String dateOfTheTask = getDateFromCalendar(calendar, year,
                maxDaysOfDec, 12);
        return dateOfTheTask;
    }

    private String getDateForThisMonth(Calendar calendar, int month, int year) {

        int maxDaysOfAMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        String dateOfTheTask = getDateFromCalendar(calendar, year,
                maxDaysOfAMonth, month);
        return dateOfTheTask;
    }

    private String getDateFromCalendar(Calendar calendar, int year, int day,
            int month) {

        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        setDateIntoCalendar(day, month - 1, year, calendar);
        String dateOfTheTask = dateFormat.format(calendar.getTime());

        return dateOfTheTask;
    }

    /**
     * spot date of next mon-sun could be in short form(mon-sun) or
     * monday-sunday
     * 
     * @param userInput
     * @param storageOfDate
     * @return date in DD/MM/YYYY format
     */
    private void spotNextWeekdayApartKeyWord(String userInput) {
        String dateOfTheTask = "", nextWeekdayInput = "";
        Pattern dateDetector = Pattern.compile(NEXT_WEEKDAY_APART_KEYWORD);
        Matcher containDate = dateDetector.matcher(inputLeft);
        Matcher toGetIndex = dateDetector.matcher(userInput);

        while (containDate.find() && toGetIndex.find()) {
            nextWeekdayInput = containDate.group();
            inputLeft = inputLeft.replaceAll(nextWeekdayInput, "");

            Calendar calendar = Calendar.getInstance();
            int todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

            nextWeekdayInput = nextWeekdayInput.trim();
            nextWeekdayInput = removeUnwantedParts(nextWeekdayInput);

            int dayOfTheWeek = WeekDayParser.detectDayOfWeek(nextWeekdayInput);

            dateOfTheTask = getNextWeekayDate(dayOfTheWeek, todayDayOfWeek);
            storageOfDate.add(dateOfTheTask);

            int indexMatched = toGetIndex.start();
            setThePosition(indexMatched);
        }
    }

    /**
     * get the final date after day of weeks is being added
     * 
     * @param dateOfTheTask
     * @param dayOfTheWeek
     * @param todayDayOfWeek
     * @return date in DD/MM/YYYY
     */
    private String getNextWeekayDate(int dayOfTheWeek, int todayDayOfWeek) {
        String dateOfTheTask = "";

        if (todayDayOfWeek == dayOfTheWeek) {
            dateOfTheTask = addToTheCurrentDateByDays(WEEK_UNIT);
        } else {
            dateOfTheTask = addToTheCurrentDateByDays(WEEK_UNIT
                    - todayDayOfWeek + dayOfTheWeek);
        }

        return dateOfTheTask;
    }

    /**
     * detect after no weeks, after no months, after no years s is not sensitive
     * 
     * @param userInput
     */
    private void spotWeekMonthYearApartKeyword(String userInput) {
        String dateOfTheTask = "";

        Pattern dateDetector = Pattern
                .compile(WEEKS_MONTHS_YEARS_APART_KEYWORD);
        Matcher containDate = dateDetector.matcher(inputLeft);
        Matcher matchWithIndex = dateDetector.matcher(userInput);

        while (containDate.find() && matchWithIndex.find()) {
            String uniqueKeyword = containDate.group();
            inputLeft = inputLeft.replaceAll(uniqueKeyword, "");

            if (uniqueKeyword.contains("week") || uniqueKeyword.contains("wk")) {

                dateOfTheTask = getWeekApartDate(uniqueKeyword);
            } else if (uniqueKeyword.contains("month")
                    || uniqueKeyword.contains("mth")) {

                dateOfTheTask = getMonthApartDate(uniqueKeyword);
            } else if (uniqueKeyword.contains("year")
                    || uniqueKeyword.contains("yr")) {

                dateOfTheTask = getYearApartDate(uniqueKeyword);
            }

            storageOfDate.add(dateOfTheTask);

            int indexMatched = matchWithIndex.start();
            setThePosition(indexMatched);
        }
    }

    /**
     * get the date after number of year
     * 
     * @param uniqueKeyword
     * @return date in dd/mm/yyyy
     */
    private String getYearApartDate(String uniqueKeyword) {
        String dateOfTheTask;
        int numberOfYears;
        numberOfYears = getNumberOfYearsDetected(uniqueKeyword);
        dateOfTheTask = addToTheCurrentDateByYear(numberOfYears);
        return dateOfTheTask;
    }

    /**
     * get date after num of month
     * 
     * @param uniqueKeyword
     * @return date in dd/mm/yyyy
     */
    private String getMonthApartDate(String uniqueKeyword) {
        String dateOfTheTask;
        int numberOfMonths;
        numberOfMonths = getNumberOfMonthDetected(uniqueKeyword);
        dateOfTheTask = addToTheCurrentDateByMonth(numberOfMonths);
        return dateOfTheTask;
    }

    /**
     * get the date after number of week
     * 
     * @param uniqueKeyword
     * @return date in dd/mm/yyyy
     */
    private String getWeekApartDate(String uniqueKeyword) {
        String dateOfTheTask;
        int numberOfDays;
        numberOfDays = getNumberOfDaysDetected(uniqueKeyword);
        dateOfTheTask = addToTheCurrentDateByDays(numberOfDays);
        return dateOfTheTask;
    }

    /**
     * spot date of this mon-sun could be in short form(mon-sun) or
     * monday-sunday
     * 
     * @param userInput
     */
    private void spotThisWeekdayApartKeyWord(String userInput) {
        String dateOfTheTask = "", thisWeekdayInput = "";

        Pattern dateDetector = Pattern.compile(THIS_WEEKDAY_APART_KEYWORD);
        Matcher containDate = dateDetector.matcher(inputLeft);
        Matcher toGetIndex = dateDetector.matcher(userInput);

        while (containDate.find() && toGetIndex.find()) {
            thisWeekdayInput = containDate.group();
            inputLeft = inputLeft.replaceAll(thisWeekdayInput, "");
            thisWeekdayInput = thisWeekdayInput.trim();
            Calendar calendar = Calendar.getInstance();
            int todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

            thisWeekdayInput = removeUnwantedParts(thisWeekdayInput);

            int dayOfTheWeek = WeekDayParser.detectDayOfWeek(thisWeekdayInput);

            dateOfTheTask = getThisWeekayDate(dayOfTheWeek, todayDayOfWeek);
            storageOfDate.add(dateOfTheTask);

            int indexMatched = toGetIndex.start();
            setThePosition(indexMatched);
        }
    }

    /**
     * get this weekday date
     * 
     * @param dateOfTheTask
     * @param dayOfTheWeek
     * @param todayDayOfWeek
     * @return DD/MM/YYYY
     */
    private String getThisWeekayDate(int dayOfTheWeek, int todayDayOfWeek) {
        String dateOfTheTask = "";

        dateOfTheTask = addToTheCurrentDateByDays(dayOfTheWeek - todayDayOfWeek);

        if (todayDayOfWeek > dayOfTheWeek) {
            JOptionPane.showMessageDialog(null,
                    "Take note: Weekday entered have passed.");
        }

        return dateOfTheTask;
    }

    /**
     * detect tomorrow, the following day, after tomorrow, after today
     * 
     * @param userInput
     */
    private void spotDaysApartVocab(String userInput) {
        String dateOfTheTask = "";

        Pattern dateDetector = Pattern.compile(DAYS_APART_VOCAB_KEYWORD);
        Matcher containDate = dateDetector.matcher(inputLeft);
        Matcher toGetIndex = dateDetector.matcher(userInput);

        while (containDate.find() && toGetIndex.find()) {
            String daysApartVocab = containDate.group();
            inputLeft = inputLeft.replaceAll(DAYS_APART_VOCAB_KEYWORD, "");

            int numberOfDays = getNumberOfDaysDetected(daysApartVocab);

            dateOfTheTask = addToTheCurrentDateByDays(numberOfDays);
            storageOfDate.add(dateOfTheTask);

            int indexMatch = toGetIndex.start();
            setThePosition(indexMatch);
        }

    }

    /**
     * set the start and end date in the right position in arrayList
     * 
     * @param indexMatch
     */
    private void setThePosition(int indexMatch) {
        String temp;

        if (storageOfDate.size() == 2 && indexMatch < index) {
            temp = storageOfDate.get(0);
            storageOfDate.set(0, storageOfDate.get(1));
            storageOfDate.set(1, temp);
        }

        index = indexMatch;
    }

    private String addToTheCurrentDateByDays(int numberOfDay) {
        String dateOfTheTask = "";

        DateFormat date = new SimpleDateFormat(DATE_FORMAT);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, numberOfDay);
        dateOfTheTask = date.format(cal.getTime());
        return dateOfTheTask;
    }

    /**
     * detect date: after (no. in word or int) days, next (no in word or int)
     * day is okay to have s behind or no s behind day
     * 
     * @param userInput
     */
    private void spotAfterDaysApartKeyword(String userInput) {
        String uniqueKeyword = "", dateOfTask = "";

        Pattern dateDetector = Pattern.compile(AFTER_DAYS_APART_KEYWORD);
        Matcher containDate = dateDetector.matcher(inputLeft);
        Matcher toGetIndex = dateDetector.matcher(userInput);

        while (containDate.find() && toGetIndex.find()) {
            uniqueKeyword = containDate.group();
            inputLeft = inputLeft.replaceAll(AFTER_DAYS_APART_KEYWORD, "");

            int numberOfDays = getNumberOfDaysDetected(uniqueKeyword);
            dateOfTask = addToTheCurrentDateByDays(numberOfDays);
            storageOfDate.add(dateOfTask);

            int indexMatched = toGetIndex.start();
            setThePosition(indexMatched);
        }
    }

    private int getNumberOfDaysDetected(String uniqueKeyword) {
        int numberOfDays = 0;

        uniqueKeyword = uniqueKeyword.trim();
        if (uniqueKeyword.equals("tmr")) {
            numberOfDays = 1;
        } else if (uniqueKeyword.contains("following day")) {
            numberOfDays = 1;
        } else if (uniqueKeyword.equals("after today")) {
            numberOfDays = 1;
        } else if (uniqueKeyword.equals("after tomorrow")
                || uniqueKeyword.equals("after tmr")) {
            numberOfDays = 2;
        } else if (uniqueKeyword.contains("week")
                || uniqueKeyword.contains("wk")) {

            uniqueKeyword = uniqueKeyword.replaceAll("\\bafter\\b", "");
            int numberOfWeek = NumberParser.getNumber(uniqueKeyword);
            numberOfDays = WEEK_UNIT * numberOfWeek;

        } else if (uniqueKeyword.contains("fortnight")) {

            int numberOfFornight = NumberParser.getNumber(uniqueKeyword);
            numberOfDays = FORTNIGHT_UNIT * numberOfFornight;
        } else {

            uniqueKeyword = uniqueKeyword.replaceAll("\\bafter\\b", "");
            numberOfDays = NumberParser.getNumber(uniqueKeyword);

        }

        return numberOfDays;
    }

    private int getNumberOfMonthDetected(String uniqueKeyword) {
        int numberOfMonth = 0;

        uniqueKeyword = uniqueKeyword.replaceAll("\\bafter\\b", "");

        if (uniqueKeyword.contains("month") || uniqueKeyword.contains("mth")) {
            numberOfMonth = NumberParser.getNumber(uniqueKeyword);
        }

        return numberOfMonth;
    }

    private int getNumberOfYearsDetected(String uniqueKeyword) {
        int numberOfYear = 0;

        uniqueKeyword = uniqueKeyword.replaceAll("\\bafter\\b", "");

        if (uniqueKeyword.contains("year") || uniqueKeyword.contains("yr")) {
            numberOfYear = NumberParser.getNumber(uniqueKeyword);
        }

        return numberOfYear;
    }

    private String addToTheCurrentDateByYear(int numberOfYear) {
        String dateOfTheTask = "";

        DateFormat date = new SimpleDateFormat(DATE_FORMAT);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, numberOfYear);
        dateOfTheTask = date.format(cal.getTime());

        return dateOfTheTask;
    }

    private String addToTheCurrentDateByMonth(int numberOfMonth) {
        String dateOfTheTask = "";

        DateFormat date = new SimpleDateFormat(DATE_FORMAT);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, numberOfMonth);
        dateOfTheTask = date.format(cal.getTime());

        return dateOfTheTask;
    }

    /**
     * detect DD Month in word/DD Month in word YYYY with space in between or no
     * space in between DD could be in 2 or 2nd or 2nd of , 2 or 3th or 3th of,
     * 4 or 4th or 4th of etc
     * 
     * @throws IllegalArgumentException
     *             : invalid month and exceed day in that month
     * @param userInput
     */
    private void spotDDMonthInWordYYYY(String userInput, String keyword)
            throws IllegalArgumentException {
        Pattern dateDetector = Pattern.compile(keyword);
        Matcher containDate = dateDetector.matcher(inputLeft);
        Matcher toGetIndex = dateDetector.matcher(userInput);

        while (containDate.find() && toGetIndex.find()) {
            Calendar calendar = Calendar.getInstance();

            String dateOfTheTask = containDate.group();
            dateOfTheTask = removeUnwantedParts(dateOfTheTask);
            int day = NumberParser.getNumber(dateOfTheTask);
            int year = YearParser.getYear(dateOfTheTask);
            int month = MonthParser.convertMonthToNumber(dateOfTheTask);

            if (day == 0) {
                break;
            }
            assert month != 0 : "month need to be detected or keyed by user";

            testValidMonth(month);
            testValidDay(day, year, month);

            dateOfTheTask = getDateFromCalendar(calendar, year, day, month);
            storageOfDate.add(dateOfTheTask);
            inputLeft = inputLeft.replaceAll(keyword, "");

            int indexOfDatePosition = toGetIndex.start();
            setThePosition(indexOfDatePosition);
        }
    }

    private void setDateIntoCalendar(int day, int month, int year,
            Calendar calendar) {
        calendar.set(Calendar.DATE, day);
        if (year != 0) {
            calendar.set(Calendar.YEAR, year);
        }
        calendar.set(Calendar.MONTH, month);
    }

    /**
     * detct DD/MM and DD/MM/YYYY
     * 
     * @param userInput
     * @return date dd/mm/yyyy
     * @throws IllegalArgumentException
     *             : max day or month exceeded
     */
    private void spotDDMMYYYYKeyword(String userInput)
            throws IllegalArgumentException {
        Pattern dateDetector = Pattern.compile(DDMMYYYY_KEYWORD);
        Matcher containDate = dateDetector.matcher(inputLeft);
        Matcher toGetIndex = dateDetector.matcher(userInput);

        while (containDate.find() && toGetIndex.find()) {
            Calendar calendar = Calendar.getInstance();
            inputLeft = inputLeft.replaceAll(DDMMYYYY_KEYWORD, "");

            String dateOfTheTask = containDate.group();

            dateOfTheTask = removeUnwantedParts(dateOfTheTask);

            int day = NumberParser.getNumber(dateOfTheTask);
            int year = YearParser.getYear(dateOfTheTask);
            int month = MonthParser.getMonth(dateOfTheTask);

            testValidMonth(month);
            testValidDay(day, year, month);

            dateOfTheTask = getDateFromCalendar(calendar, year, day, month);
            storageOfDate.add(dateOfTheTask);

            int indexPositionOfThisDate = toGetIndex.start();
            setThePosition(indexPositionOfThisDate);
        }
    }

    /**
     * throws and catch exception of invalid month
     * 
     * @param month
     * @throws IllegalArgumentException
     *             : month exceed 12
     */
    private void testValidMonth(int month) throws IllegalArgumentException {
        Logger logger = Logger.getLogger("DateParser");
        try {
            logger.log(Level.INFO,
                    "going to start processing for invalid month");

            if (month <= 0 || month > 12) {
                throw new IllegalArgumentException("Invalid Month Keyed!");
            }
        } catch (IllegalArgumentException e) {
            logger.log(Level.FINER, "Invalid Month Keyed!");
            JOptionPane.showMessageDialog(null, e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * check if the date is valid if the year and the month have this day For
     * example feb only have max 29 days
     * 
     * @param day
     * @throws IllegalArgumentException
     *             : day exceed max day
     */
    private void testValidDay(int day, int year, int month)
            throws IllegalArgumentException {
        Logger logger = Logger.getLogger("DateParser");
        try {
            logger.log(Level.INFO,
                    "going to start processing for invalid day that pass max day");
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
            logger.log(Level.FINER,
                    "Invalid Day Keyed! Exceed the maximum day in that month");
            JOptionPane.showMessageDialog(null, e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String removeUnwantedParts(String timeWithUnwantedPart) {
        String time;
        timeWithUnwantedPart = timeWithUnwantedPart.trim();
        time = timeWithUnwantedPart.replaceAll(TO_BE_REMOVED_KEYWORD, "");
        return time;
    }
}
