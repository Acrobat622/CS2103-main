package parser;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

/**
 * add in missing time: 1) user keyed: 2 dates (today, dates after today) :
 * current time 23:59 2) user keyed: 2 dates (dates after today, dates after
 * today) : 00:00 23:59 3) user keyed: 2 dates (yesterday, today/date after
 * today) : 23:59 23:59 4) user Keyed: 1 date (today / dates after today) :
 * 23:59 5) user keyed: 2 dates and 1 time : time detected 23:59
 * 
 * add in missing date: 1) user keyed: two times (1 before current time < 1
 * before) : tomorrow tomorrow 2) user keyed: two times (1 before current time >
 * 1 before) : tomorrow, after tomorrow 3) user keyed: two times (past current
 * time < past current time) : today, today 4) user keyed: two times (past
 * current time > past current time) : today, tomorrow 5) user keyed: two times
 * (1 before current time and 1 after) : tomorrow, tomorrow 6) user keyed: two
 * times (1 after current time and 1 before) : today, tomorrow 7) user keyed: 2
 * times and 1 date (start time > end time) : date keyed, next day of date keyed
 * 8) user keyed: 2 times and 1 date (start time < end time) : date keyed, date
 * keyed 9) user keyed: 1 times (past current time) and 0 date : today 10) user
 * keyed: 1 times (before current time) and 0 date : tomorrow
 * 
 * Exceptions: 1) dates before current date 2) remainder when start date or time
 * is before current date or time 3) impossible combination of timed task with
 * start time or date > end time or date
 * 
 * @author A0112823R
 *
 */
public class DateTimeParser {
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String TIME_FORMAT = "HH:mm";
    private int numberOfTime;
    private String fullUserInput;
    private String description;
    private String feedback;
    private String startTime = "-", endTime = "-", startDate = "-",
            endDate = "-";
    private final String BEFORE_AFTER_MMDD_KEYWORD = "\\b(\\w+(\\s|)(day(s|)|the day(s|)"
            + "|(mon|tues|wed|thurs|fri|sat|sun)(day|nesday|urday|)(s|))"
            + "( before| after)) ((\\d+([/.]\\d+[/.-]\\d+|[/.-]\\d+)\\b)|((\\w+|)(-|)\\w+(th|nd|rd|st|)(\\s|\\S)(of |)(january|febuary|march|april|may|june|july|august"
            + "|september|octobor|november|december)(\\s|)((in (the|) (year|yr)(s|))|)(\\s|)(\\d+|))|(\\w+|)(-|)\\w+(th|nd|rd|st|)(\\s|\\S)(of |)(jan|feb|mar|apr|may|jun|jul|aug"
            + "|sep|oct|nov|dec)(\\s|\\S)((in (the|) (year|yr)(s|))|)(\\s|)(\\d+|))";
    private final String NEXT_WEEKDAY_AND_OR = "(\\b(next|this|at|by|due on|on|due) (mon|tues|wed|thurs|fri|sat|sun)(day|nesday|urday|)(s|) (or|and) "
            + "(mon|tues|wed|thurs|fri|sat|sun)(day|nesday|urday|)(s|)\\b)";
    private ArrayList<String> storageOfTime = new ArrayList<String>();
    private ArrayList<String> storageOfDate = new ArrayList<String>();

    public DateTimeParser(String userInput) throws Exception {
        fullUserInput = userInput;
        TimeParser times = extractFromTimeParser();
        int indexPrevTime = times.getPosition();

        String leftOverInput;
        DateParser dates = extractFromDateParser(times);
        int indexPrevDate = dates.getIndex();

        leftOverInput = getLeftOverInput(dates);

        extractFromNattyParser(indexPrevTime, leftOverInput, indexPrevDate);

        assert storageOfDate.size() <= 2 : "key in more than 2 dates!";
        assert storageOfTime.size() <= 2 : "key in more than 2 times!";

        testForExceptionCases(storageOfTime, storageOfDate);

        storageOfTime = addInMissingTime(storageOfTime, storageOfDate);
        storageOfDate = addInMissingDate(storageOfTime, storageOfDate);

        addWeekDayToDate(storageOfDate);
        setNumberOfTimeDetected(storageOfTime);
        setAllParameters(storageOfTime, storageOfDate);
    }

    private void extractFromNattyParser(int indexPrevTime,
            String leftOverInput, int indexPrevDate) {

        DateTimeNattyParser dateTimeNatty = new DateTimeNattyParser();
        dateTimeNatty.extractDateTime(fullUserInput, leftOverInput,
                storageOfDate, storageOfTime, indexPrevTime, indexPrevDate);

        storageOfTime = dateTimeNatty.getTimeList();
        storageOfDate = dateTimeNatty.getDateList();
        description = dateTimeNatty.getDescription();
    }

    private TimeParser extractFromTimeParser() throws Exception {

        TimeParser times = new TimeParser(fullUserInput);
        storageOfTime = times.getTimeList();

        return times;
    }

    private DateParser extractFromDateParser(TimeParser times) {
        DateParser dates = new DateParser();
        String leftOverInput = removeComplication(times.getInputLeft());
        dates.extractDate(fullUserInput, leftOverInput);
        storageOfDate = dates.getDateList();

        return dates;
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
     * make changes to the userInput, so that it will bypass detection from
     * dateParser and detect by natty which could only detect correctly by natty
     * 
     * @param dates
     * @return userInput
     */
    private String getLeftOverInput(DateParser dates) {
        fullUserInput = switchAllToLowerCase(fullUserInput);
        String leftOverInput = convertDDMMToMMDD(dates.getInputLeft());
        leftOverInput = getRemovedKeyword(leftOverInput);
        return leftOverInput;
    }

    /**
     * add back the removal keyword that will detect by Date1Parser but only
     * natty could parse it correctly
     * 
     * @param userInput
     * @param leftOverInput
     * @return leftOverInput only for natty to detect
     */
    private String getRemovedKeyword(String leftOverInput) {
        Pattern detector = Pattern.compile(NEXT_WEEKDAY_AND_OR);
        Matcher containComplicationWord = detector.matcher(fullUserInput);

        while (containComplicationWord.find()) {
            String word = "";
            word = containComplicationWord.group();
            leftOverInput = leftOverInput + " " + word;
        }

        return leftOverInput;
    }

    /**
     * so that the natty could detect correctly
     * 
     * @param userInput
     * @param inputLeft
     * @return userInput for natty to detect
     */
    private String convertDDMMToMMDD(String inputLeft) {
        Pattern detector = Pattern.compile(BEFORE_AFTER_MMDD_KEYWORD);
        Matcher containComplicationWord = detector.matcher(fullUserInput);

        while (containComplicationWord.find()) {

            String word = containComplicationWord.group();
            DateParser dateParser = new DateParser(word);
            ArrayList<String> date = dateParser.getDateList();

            assert date.size() == 1 : "only should have one date detected!";

            String[] partOfDate = date.get(0).split("/");

            assert partOfDate.length == 3 : "Detected date should be in a format of dd/mm/yyyy";

            String wordWithoutDate = dateParser.getInputLeft();

            fullUserInput = fullUserInput.replaceAll(word, wordWithoutDate
                    + " " + partOfDate[1] + "/" + partOfDate[0] + "/"
                    + partOfDate[2]);

            inputLeft = inputLeft + wordWithoutDate + " " + partOfDate[1] + "/"
                    + partOfDate[0] + "/" + partOfDate[2];
        }
        return inputLeft;
    }

    /**
     * remove complication like those will detect in Date1Parser but only could
     * be detect correctly in natty. So we remove them 1st.
     * 
     * @param userInput
     * @return userInput after removal
     */
    private String removeComplication(String userInput) {

        userInput = removeBeforeAfterDateKeyowrd(userInput);

        userInput = removeNextWeekAndOrWeekKeyword(userInput);

        return userInput;
    }

    /**
     * remove keyword of next <weekday> and/or <weekday>
     * 
     * @param userInput
     * @return the userinput after removal.
     */
    private String removeNextWeekAndOrWeekKeyword(String userInput) {
        Pattern detector = Pattern.compile(NEXT_WEEKDAY_AND_OR);
        Matcher containComplicationWord = detector.matcher(userInput);

        while (containComplicationWord.find()) {
            String word = "";
            word = containComplicationWord.group();
            userInput = userInput.replaceAll(word, "");
        }

        return userInput;
    }

    /**
     * remove keyword of _____ before/after date
     * 
     * @param userInput
     * @return userInput after removal
     */
    private String removeBeforeAfterDateKeyowrd(String userInput) {

        Pattern detector = Pattern.compile(BEFORE_AFTER_MMDD_KEYWORD);
        Matcher containComplicationWord = detector.matcher(userInput);

        while (containComplicationWord.find()) {
            String word = "";
            word = containComplicationWord.group();
            userInput = userInput.replaceAll(word, "");
        }
        return userInput;
    }

    /**
     * get the feedback for overdue date like start date or time keyed is before
     * current date but not end time and date
     * 
     * @return feedback for overdue date to logic
     */
    public String getFeedBack() {
        return feedback;
    }

    /**
     * add weekday to the start of the date into <weekDay> dd/mm/yyyy
     * 
     * @param storageOfDate
     *            :contains date in dd/mm/yyyy
     */
    private void addWeekDayToDate(ArrayList<String> storageOfDate) {

        for (int i = 0; i < storageOfDate.size(); i++) {
            if (storageOfDate.get(i).substring(0, 1).matches("\\d")) {
                String weekDay = WeekDayParser.getWeekDay(storageOfDate.get(i));
                storageOfDate.set(i, weekDay + " " + storageOfDate.get(i));
            }
        }

    }

    private void setAllParameters(ArrayList<String> storageOfTime,
            ArrayList<String> storageOfDate) {

        setAllParametersToDash();
        if (storageOfTime.size() == 1) {

            setStartTime("-");
            setEndTime(storageOfTime.get(0));

            setStartDate("-");
            setEndDate(storageOfDate.get(0));

        } else if (storageOfTime.size() == 2) {

            setStartTime(storageOfTime.get(0));
            setEndTime(storageOfTime.get(1));

            setStartDate(storageOfDate.get(0));
            setEndDate(storageOfDate.get(1));
        }
    }

    /**
     * 1) Key in same times and same dates --> don't allow user to add 2) Key in
     * time before current time on current date 3) Key in date later than
     * current date --> remind the user meeting have pass. 4) key in start time
     * before current time but end time after current time --> remind the user
     * meeting is ongoing.
     * 
     * @param storageOfTime
     * @param storageOfDate
     */
    private void testForExceptionCases(ArrayList<String> storageOfTime,
            ArrayList<String> storageOfDate) throws Exception {

        checkDateBeforeCurrent(storageOfTime, storageOfDate);
        checkOngoingTimeTask(storageOfDate, storageOfTime);
        checkImpossibleTimedTask(storageOfTime, storageOfDate);

    }

    /**
     * check impossible combination of time task For example, same day with two
     * same date keyed by user
     * 
     * @param storageOfTime
     *            : time contained all time detected
     * @param storageOfDate
     *            : date contined all date detected
     * @throws ParseException
     *             : fail to parse date
     * @throws IllegalArgumentException
     *             : type in start date later than end date
     * 
     */
    private void checkImpossibleTimedTask(ArrayList<String> storageOfTime,
            ArrayList<String> storageOfDate) throws ParseException {
        Logger logger = Logger.getLogger("DateTimeParser");
        try {
            logger.log(Level.INFO,
                    "going to start processing for impossible time task keyed");
            checkStartDateLaterWhenBothKeyed(storageOfTime, storageOfDate);
            checkStartDateLaterWhenTimeNotKeyed(storageOfDate);

        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "processing error", e);
            JOptionPane.showMessageDialog(null, e.getMessage());
            throw new IllegalArgumentException(e.getMessage());

        }
    }

    /**
     * check that on same day is start time is later than end and same time and
     * same date. if yes, throw IllegalArgumentException
     * 
     * @param storageOfTime
     * @param storageOfDate
     * @throws ParseException
     *             : fail to parse date
     * @throws IllegalArgumentException
     *             : type in start date later than end date
     */
    private void checkStartDateLaterWhenBothKeyed(
            ArrayList<String> storageOfTime, ArrayList<String> storageOfDate)
            throws ParseException {
        if (storageOfTime.size() == 2 && storageOfDate.size() == 2) {

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "dd/MM/YYYY HH:mm");
            Date startDate = dateFormat.parse(storageOfDate.get(0) + " "
                    + storageOfTime.get(0));
            Date endDate = dateFormat.parse(storageOfDate.get(1) + " "
                    + storageOfTime.get(1));

            if (startDate.after(endDate) || startDate.equals(endDate)) {
                throw new IllegalArgumentException(
                        "Impossible combination for timed task! End time must be later than start time on the same day");
            }
        }
    }

    /**
     * check that start date later than end date, if yes, throws
     * IllegalArgumentException
     * 
     * @param storageOfDate
     * @throws ParseException
     *             : fail to parse date
     * @throws IllegalArgumentException
     *             : type in start date later than end date
     */
    private void checkStartDateLaterWhenTimeNotKeyed(
            ArrayList<String> storageOfDate) throws ParseException {
        if (storageOfDate.size() == 2) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            Date date1 = dateFormat.parse(storageOfDate.get(0));
            Date date2 = dateFormat.parse(storageOfDate.get(1));

            if (date1.after(date2)) {
                throw new IllegalArgumentException(
                        "Impossible combination for timed task! End date must be later than start date");
            }
        }
    }

    /**
     * check 1) user key in overdue time that before current time 2) user key in
     * date before current date
     * 
     * @param storageOfTime
     *            : contains time detected
     * @param storageOfDate
     *            : contains date detected
     * @throws ParseException
     *             : fail to parse date
     */
    private void checkDateBeforeCurrent(ArrayList<String> storageOfTime,
            ArrayList<String> storageOfDate) throws ParseException {

        overdueTaskDueToTime(storageOfTime, storageOfDate);

        overdueTaskDueToDate(storageOfDate);

    }

    private void overdueTaskDueToTime(ArrayList<String> storageOfTime,
            ArrayList<String> storageOfDate) throws ParseException {
        if (storageOfTime.size() > 0) {

            int numberBeforeCurrentTime = countNumberBeforeCurrentTime(storageOfTime);

            if (storageOfTime.size() == 1
                    && numberBeforeCurrentTime == 1
                    && (storageOfDate.size() == 1 && storageOfDate.get(0)
                            .equals(getCurrentDate()))) {

                feedback = "Time keyed past the current time!!";
                JOptionPane.showMessageDialog(null,
                        "Time keyed past the current time!!", "REMAINDER",
                        JOptionPane.INFORMATION_MESSAGE);

            } else if ((storageOfDate.size() == 2
                    && numberBeforeCurrentTime == 2
                    && storageOfDate.get(0).equals(getCurrentDate()) && storageOfDate
                    .get(1).equals(getCurrentDate()))) {

                feedback = "Time keyed past the current time!!";
                JOptionPane.showMessageDialog(null,
                        "Time keyed past the current time!!", "REMAINDER",
                        JOptionPane.INFORMATION_MESSAGE);

            }
        }
    }

    private void overdueTaskDueToDate(ArrayList<String> storageOfDate)
            throws ParseException {
        if (storageOfDate.size() > 0) {

            int numberBeforeCurrentDate = countNumberBeforeCurrentDate(storageOfDate);

            if (storageOfDate.size() == 1 && numberBeforeCurrentDate == 1) {

                feedback = "Date keyed past the current date!";
                JOptionPane.showMessageDialog(null,
                        "Date keyed past the current date!", "REMAINDER",
                        JOptionPane.INFORMATION_MESSAGE);

            } else if (storageOfDate.size() == 2
                    && numberBeforeCurrentDate == 2) {

                feedback = "Date keyed past the current date!";
                JOptionPane.showMessageDialog(null,
                        "Date keyed past the current date!", "REMAINDER",
                        JOptionPane.INFORMATION_MESSAGE);

            }
        }
    }

    /**
     * check if the start time is before the current time
     * 
     * @param storageOfTime
     *            : contains time detected in user input
     * @return true if the time is before current time otherwise false.
     * @throws ParseException
     *             : fail to parse date
     */
    private boolean isStartTimeBeforeCurrent(ArrayList<String> storageOfTime)
            throws ParseException {
        boolean isStartTimeBeforeCurrent = false;
        if (storageOfTime.size() >= 1) {

            String currentTime = getCurrentTime();

            SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);

            Date time = dateFormat.parse(storageOfTime.get(0));
            Calendar calendarTime = Calendar.getInstance();
            calendarTime.setTime(time);

            Date timeNow = dateFormat.parse(currentTime);
            Calendar calendarOfCurrentTime = Calendar.getInstance();
            calendarOfCurrentTime.setTime(timeNow);

            if (calendarOfCurrentTime.getTime().after(calendarTime.getTime())) {
                isStartTimeBeforeCurrent = true;
            }

        }
        return isStartTimeBeforeCurrent;
    }

    /**
     * calculate the number of time keyed past the current time
     * 
     * @param storageOfDate
     *            : stored time keyed by user in hh:mm format
     * @param numberPastCurrentDate
     *            : store the number that past current time
     * @return number of time keyed past current time
     * @throws ParseException
     *             : fail to parse date
     */
    private int countNumberBeforeCurrentTime(ArrayList<String> storageOfTime)
            throws ParseException {

        int numberBeforeCurrentTime = 0;

        for (int i = 0; i < storageOfTime.size(); i++) {
            String currentTime = getCurrentTime();

            SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);

            Date time = dateFormat.parse(storageOfTime.get(i));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);

            Date timeForNow = dateFormat.parse(currentTime);
            Calendar calendarOfCurrentTime = Calendar.getInstance();
            calendarOfCurrentTime.setTime(timeForNow);

            if (calendarOfCurrentTime.getTime().after(calendar.getTime())) {
                numberBeforeCurrentTime++;
            }

        }

        return numberBeforeCurrentTime;
    }

    /**
     * give remainder user key in task that have start date before current date
     * but end date that is after current date
     * 
     * @param storageOfDate
     *            :contains dates detected
     * @throws ParseException
     *             : fail to parse date
     */
    private void checkOngoingTimeTask(ArrayList<String> storageOfDate,
            ArrayList<String> storageOfTime) throws ParseException {

        checkOngoingDueToDate(storageOfDate, storageOfTime);

        checkOngoingDueToTime(storageOfDate, storageOfTime);
    }

    private void checkOngoingDueToTime(ArrayList<String> storageOfDate,
            ArrayList<String> storageOfTime) throws ParseException {

        boolean isStartTimeBeforeCurrent = isStartTimeBeforeCurrent(storageOfTime);

        if (storageOfTime.size() == 2 && storageOfDate.size() > 0
                && isStartTimeBeforeCurrent
                && storageOfDate.get(0).equals(getCurrentDate())) {

            feedback = "The start date keyed have past the current date but end date have not.\nGo For The Meeting!";

            JOptionPane.showMessageDialog(null,
                    "The start date keyed have past the current date but end date have not.\n"
                            + "Go For The Meeting!", "REMAINDER",
                    JOptionPane.INFORMATION_MESSAGE);

        }
    }

    private void checkOngoingDueToDate(ArrayList<String> storageOfDate,
            ArrayList<String> storageOfTime) throws ParseException {
        int numberBeforeCurrentDate = countNumberBeforeCurrentDate(storageOfDate);

        if ((storageOfDate.size() == 2 || storageOfTime.size() == 2)
                && numberBeforeCurrentDate == 1) {

            feedback = "The start date keyed have past the current date but end date have not.\nGo For The Meeting!";

            JOptionPane.showMessageDialog(null,
                    "The start date keyed have past the current date but end date have not.\n"
                            + "Go For The Meeting!", "REMAINDER",
                    JOptionPane.INFORMATION_MESSAGE);

        }

    }

    /**
     * calculate the number of date keyed past the current date
     * 
     * @param storageOfDate
     *            : stored date keyed by user in <weekday> dd/mm/yyyy format
     * @param numberPastCurrentDate
     *            : store the number that past current date
     * @return number of date keyed past current day
     * @throws ParseException
     *             : fail to parse date
     */
    private int countNumberBeforeCurrentDate(ArrayList<String> storageOfDate)
            throws ParseException {
        int numberBeforeCurrentDate = 0;

        for (int i = 0; i < storageOfDate.size(); i++) {

            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

            Date date = dateFormat.parse(storageOfDate.get(i));
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(date);

            Calendar todayCalendar = Calendar.getInstance();
            String currentDate = getCurrentDate();

            Date dateOfToday = dateFormat.parse(currentDate);
            todayCalendar.setTime(dateOfToday);

            if (todayCalendar.getTime().after(dateCalendar.getTime())) {
                numberBeforeCurrentDate++;
            }

        }
        return numberBeforeCurrentDate;
    }

  
    private void setAllParametersToDash() {
        setStartTime("-");
        setEndTime("-");
        setStartDate("-");
        setEndDate("-");
    }

   
    private void setStartDate(String date) {
        startDate = date;
    }

   
    public String getStartDate() {
        return startDate;
    }

    private void setEndDate(String date) {
        endDate = date;
    }

    public String getEndDate() {
        return endDate;
    }

    private void setEndTime(String time) {
        endTime = time;
    }
  
    public String getEndTime() {
        return endTime;
    }

    private void setStartTime(String time) {
        startTime = time;
    }

    public String getStartTime() {
        return startTime;
    }

    /**
     * add in the time that is not keyed in by the user and by the the right
     * interpreted
     * 
     * @param storageOfTime
     *            :contains all time detected
     * @param storageOfDate
     *            :conatains all date detected
     * @throws ParseException
     */
    private ArrayList<String> addInMissingTime(ArrayList<String> storageOfTime,
            ArrayList<String> storageOfDate) throws ParseException {

        if (storageOfTime.size() < storageOfDate.size()) {

            if (storageOfTime.size() == 0 && storageOfDate.size() == 1) {

                storageOfTime.add("23:59");

            } else if (storageOfTime.size() == 0 && storageOfDate.size() == 2) {

                addTimeWhenNoTimeAndTwoDate(storageOfTime, storageOfDate);

            } else if (storageOfTime.size() == 1 && storageOfDate.size() == 2) {

                storageOfTime.add("23:59");

            }
        }
        return storageOfTime;
    }

    /**
     * add missing two times when 2 dates and no time is detected
     * 
     * @param storageOfTime
     *            : contains time detected
     * @param storageOfDate
     *            : contains date detected
     * @throws ParseException
     */
    private void addTimeWhenNoTimeAndTwoDate(ArrayList<String> storageOfTime,
            ArrayList<String> storageOfDate) throws ParseException {

        int numberBeforeCurrentDate = countNumberBeforeCurrentDate(storageOfDate);
        String currentTime = getCurrentTime();

        // today - today, today - tomorrow
        if (storageOfDate.get(0).equals(getCurrentDate())) {

            storageOfTime.add(currentTime);
            storageOfTime.add("23:59");

            // yesterday - today
        } else if (numberBeforeCurrentDate == 1) {

            storageOfTime.add("23:59");
            storageOfTime.add("23:59");

        } else {

            storageOfTime.add("00:00");
            storageOfTime.add("23:59");

        }
    }

    /**
     * add in the date that is not keyed in by the user and by the the right
     * interpreted
     * 
     * @param storageOfTime
     *            :time detected by user
     * @param storageOfDate
     *            date detected by user
     * @throws ParseException
     */
    private ArrayList<String> addInMissingDate(ArrayList<String> storageOfTime,
            ArrayList<String> storageOfDate) throws ParseException {

        int numberBeforeCurrentTime = countNumberBeforeCurrentTime(storageOfTime);
        boolean isStartTimeBeforeCurrent = isStartTimeBeforeCurrent(storageOfTime);

        String currentDate = getCurrentDate();
        String nextDayDate = addDateToNumberOfDay(1, currentDate);
        String afterTwoDaysDate = addDateToNumberOfDay(2, currentDate);

        if (storageOfDate.size() < storageOfTime.size()) {

            if (storageOfDate.size() == 0 && storageOfTime.size() == 1) {

                addDateWhenZeroDateAndOneTime(storageOfDate,
                        numberBeforeCurrentTime, currentDate, nextDayDate);

            } else if (storageOfDate.size() == 0 && storageOfTime.size() == 2) {

                addDatesWhenZeroDateAndTwoTime(storageOfTime, storageOfDate,
                        numberBeforeCurrentTime, isStartTimeBeforeCurrent,
                        currentDate, nextDayDate, afterTwoDaysDate);

            } else if (storageOfDate.size() == 1 && storageOfTime.size() == 2) {

                addDateWhenOneDateAndTwoTimes(storageOfTime, storageOfDate,
                        numberBeforeCurrentTime, currentDate, nextDayDate);
            }

        }
        return storageOfDate;
    }

    /**
     * add dates when 0 date and 1 time was detected
     * 
     * @param storageOfDate
     *            : to be store with date
     * @param numberBeforeCurrentTime
     *            : number of time before current time
     * @param currentDate
     * @param nextDayDate
     *            : tomorrow date
     */
    private void addDateWhenZeroDateAndOneTime(ArrayList<String> storageOfDate,
            int numberBeforeCurrentTime, String currentDate, String nextDayDate) {

        if (numberBeforeCurrentTime == 1) {
            storageOfDate.add(nextDayDate);

        } else {

            storageOfDate.add(currentDate);

        }
    }

    /**
     * add when 1 dates and 2 times was detected
     * 
     * @param storageOfTime
     *            : contain the times detected
     * @param storageOfDate
     *            : to be store with date
     * @param numberBeforeCurrentTime
     *            : number of time before current time
     * @param currentDate
     * @param nextDayDate
     *            : tomorrow date
     * @throws ParseException
     *             : fail to parse date
     */
    private void addDateWhenOneDateAndTwoTimes(ArrayList<String> storageOfTime,
            ArrayList<String> storageOfDate, int numberBeforeCurrentTime,
            String currentDate, String nextDayDate) throws ParseException {

        if (storageOfTime.get(0).equals(storageOfTime.get(1))
                || isStartTimeLaterThanEnd(storageOfTime)) {

            storageOfDate.add(addDateToNumberOfDay(1, storageOfDate.get(0)));

        } else if (storageOfDate.get(0).equals(currentDate)
                && numberBeforeCurrentTime == 2) {

            storageOfDate.add(nextDayDate);

        } else if (!isStartTimeLaterThanEnd(storageOfTime)) {

            storageOfDate.add(storageOfDate.get(0));
        }
    }

    /**
     * add dates when 0 dates and 2 time is detected
     * 
     * @param storageOfTime
     *            :contains of time detcted
     * @param storageOfDate
     *            :to be stored with the new date
     * @param numberBeforeCurrentTime
     *            : number of times detected before current time
     * @param isStartTimeBeforeCurrent
     *            : true if start time is before current time
     * @param currentDate
     * @param nextDayDate
     * @param afterTwoDaysDate
     * @throws ParseException
     *             : fail to parse date
     */
    private void addDatesWhenZeroDateAndTwoTime(
            ArrayList<String> storageOfTime, ArrayList<String> storageOfDate,
            int numberBeforeCurrentTime, boolean isStartTimeBeforeCurrent,
            String currentDate, String nextDayDate, String afterTwoDaysDate)
            throws ParseException {

        if (storageOfTime.get(0).equals(storageOfTime.get(1))
                || (!isStartTimeBeforeCurrent && numberBeforeCurrentTime == 1)
                || (numberBeforeCurrentTime == 0 && isStartTimeLaterThanEnd(storageOfTime))) {

            storageOfDate.add(currentDate);
            storageOfDate.add(nextDayDate);

        } else if ((numberBeforeCurrentTime == 1 && isStartTimeBeforeCurrent)
                || (numberBeforeCurrentTime == 2 && !isStartTimeLaterThanEnd(storageOfTime))) {

            storageOfDate.add(nextDayDate);
            storageOfDate.add(nextDayDate);

        } else if (numberBeforeCurrentTime == 2
                && isStartTimeLaterThanEnd(storageOfTime)) {

            storageOfDate.add(nextDayDate);
            storageOfDate.add(afterTwoDaysDate);

        } else if (numberBeforeCurrentTime == 0
                && !isStartTimeLaterThanEnd(storageOfTime)) {

            storageOfDate.add(currentDate);
            storageOfDate.add(currentDate);
        }
    }

    /**
     * check if start time is later than end time
     * 
     * @param storageOfTime
     *            : contain time detected
     * @return true if start time later than end time otherwise false
     * @throws ParseException
     */
    private boolean isStartTimeLaterThanEnd(ArrayList<String> storageOfTime)
            throws ParseException {

        boolean isStartTimeLaterThanEnd = false;

        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);

        Date startTime = timeFormat.parse(storageOfTime.get(0));
        Calendar startTimeCalendar = Calendar.getInstance();
        startTimeCalendar.setTime(startTime);

        Date endTime = timeFormat.parse(storageOfTime.get(1));
        Calendar endTimecalendar = Calendar.getInstance();
        endTimecalendar.setTime(endTime);

        if (startTimeCalendar.getTime().after(endTimecalendar.getTime())) {
            isStartTimeLaterThanEnd = true;
        }

        return isStartTimeLaterThanEnd;
    }

    private static String getCurrentDate() {

        DateFormat date = new SimpleDateFormat(DATE_FORMAT);
        Calendar cal = Calendar.getInstance();
        String dateOfTheTask = date.format(cal.getTime());

        return dateOfTheTask;
    }

    /**
     * get date after adding the number of days
     * 
     * @param numberOfDay
     * @return DD/MM/YYYY
     * @throws ParseException
     *             : fail to parse
     */
    private static String addDateToNumberOfDay(int numberOfDay, String date)
            throws ParseException {

        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date date1 = null;

        try {
            date1 = dateFormat.parse(date);

        } catch (ParseException e) {

            JOptionPane.showMessageDialog(null, e.getMessage());
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);

        cal.add(Calendar.DATE, numberOfDay);

        String dateOfTheTask = dateFormat.format(cal.getTime());

        return dateOfTheTask;
    }

    /**
     * get the current time in HH:MM
     * 
     * @return time in HH:MM
     */
    private static String getCurrentTime() {

        DateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);
        Calendar cal = Calendar.getInstance();
        String timeOfTheTask = dateFormat.format(cal.getTime());

        return timeOfTheTask;
    }

    public void setNumberOfTimeDetected(ArrayList<String> storageOfParameters) {
        numberOfTime = storageOfParameters.size();
    }
  
    public int getNumberOfTimeDetected() {
        return numberOfTime;
    }

    /**
     * 
     * @return userInput after the detection of all time and date
     */
    public String getUserInputLeft() {
        return description;
    }
}
