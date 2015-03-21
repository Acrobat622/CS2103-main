package parser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DateTimeParser {
	private static final String DATE_FORMAT = "dd/MM/yyyy";
	private static final String TIME_FORMAT = "HH:mm";
	private static int numberOfTime;
	private static String deadlineTime = "-";
	private static String deadlineDate = "-";
	private static String startTime = "-", endTime = "-", startDate = "-", endDate = "-";  


	public DateTimeParser() {

	}

	/**
	 * get time and date and form them into a string
	 * @param userInput
	 * @return date and time in a string
	 */
	public static String getDateTime(String userInput) {
		String dateTime = "";
		ArrayList<String> storageOfTime = TimeParser.extractTime(userInput);
		ArrayList<String> storageOfDate = DateParser.extractDate(userInput);

		storageOfTime = addInMissingTime(storageOfTime, storageOfDate);
		storageOfDate = addInMissingDate(storageOfTime, storageOfDate);
		setNumberOfTime(storageOfTime);

		if (storageOfTime.size() == 1) {
			setDeadlineTime (storageOfTime.get(0));
			setDeadlineDate(storageOfDate.get(0));
		} else if (storageOfTime.size() == 2) {
			setStartTime(storageOfTime.get(0));
			setEndTime(storageOfTime.get(1));
			setStartDate(storageOfDate.get(0));
			setEndDate(storageOfDate.get(1));
		}
		storageOfTime = addDashWhenEmpty(storageOfTime);
		storageOfDate = addDashWhenEmpty(storageOfDate);


		System.out.println ("SOT: " + storageOfTime);
		dateTime = formDateTime(storageOfTime, storageOfDate);

		assert storageOfTime.size() > 2 && storageOfDate.size() > 2 && 
		storageOfTime.size() != storageOfDate.size();

		System.out.println ("dateTime "+ dateTime + " SOT: " + storageOfTime);
		return dateTime;
	}

	/**
	 * set start date
	 * @param date
	 */
	private static void setStartDate(String date) {
		startDate = date;		
	}

	/**
	 * get start date 
	 * @return start date for timed task
	 */
	static String getStartDate() {
		return startDate;
	}

	/**
	 * set the end date
	 * @param date
	 */
	private static void setEndDate(String date) {
		endDate = date;	
	}

	/**
	 * get end end
	 * @return end date for timed
	 */
	static String getEndDate() {
		return endDate;
	}


	/**
	 * set end time
	 * @param time
	 */
	private static void setEndTime(String time) {
		endTime = time;	
	}

	/**
	 * get end time
	 * @return end time 
	 */
	static String getEndTime() {
		return endTime;
	}

	/**
	 * set the start time for timed task
	 * @param time
	 */
	private static void setStartTime(String time) {
		startTime = time;	
	}


	/**
	 * return start time for timed task
	 * @return start time
	 */
	static String getStartTime() {
		return startTime;
	}

	/**
	 * set the deadline date
	 * @param string
	 */
	private static void setDeadlineDate(String date) {
		deadlineDate = date;

	}
	
	/**
	 * get date for deadline 
	 * @return deadline date
	 */
	static String getDeadlineDate() {
		return deadlineDate;
	}


	/**
	 * set the deadline time if the taskType is deadline. 
	 * @param string
	 */
	private static void setDeadlineTime(String time) {
		deadlineTime = time;	
	}

	/**
	 * get deadline time
	 * @return time
	 */
	static String getDeadlineTime() {
		return deadlineTime;
	}
	
	/**
	 * add in - when the arrayList is empty
	 * @param storageOfParameter
	 * @return arrayList of dash.
	 */
	private static ArrayList<String> addDashWhenEmpty(
			ArrayList<String> storageOfParameter) {
		if (storageOfParameter.isEmpty()) {
			storageOfParameter.add("-");
		}
		return storageOfParameter;
	}

	/**
	 * add in the time that is not keyed in by the user and by the the right interpreted
	 * @param storageOfTime
	 * @param storageOfDate
	 */
	private static ArrayList<String> addInMissingTime(
			ArrayList<String> storageOfTime, ArrayList<String> storageOfDate) {
		if(storageOfTime.size() < storageOfDate.size()) {
			if(storageOfTime.size() == 0 && storageOfDate.size() == 1) {
				storageOfTime.add("23:59");
			} else if (storageOfTime.size() == 0 && storageOfDate.size() == 2) {
				String currentTime = getCurrentTime();
				storageOfTime.add(currentTime);
				storageOfTime.add(addOneHourToTime(currentTime));
			} else if(storageOfTime.size() == 1 && storageOfDate.size() == 2) {
				storageOfTime.add(addOneHourToTime(storageOfTime.get(0)));
			}
		}
		return storageOfTime;
	}

	/**
	 * add in the date that is not keyed in by the user and by the the right interpreted
	 * @param storageOfTime
	 * @param storageOfDate
	 */
	private static ArrayList<String> addInMissingDate(
			ArrayList<String> storageOfTime, ArrayList<String> storageOfDate) {

		if(storageOfDate.size() < storageOfTime.size()) {
			if(storageOfDate.size() == 0 && storageOfTime.size() == 1) {
				storageOfDate.add(getCurrentDate());
			} else if (storageOfDate.size() == 0 && storageOfTime.size() == 2) {
				storageOfDate.add(getCurrentDate());
				storageOfDate.add(getCurrentDate());
			} else if(storageOfDate.size() == 1 && storageOfTime.size() == 2) {
				storageOfDate.add(storageOfDate.get(0));
			}
		}
		return storageOfDate;
	}
	/**
	 * add 1 hour to the time
	 * @param time
	 * @return time in HH:MM
	 */
	private static String addOneHourToTime(String time) {
		String hourTimeInString;

		System.out.println("time: "+time);
		int hourTime =  get1stNumber(time); //take note if 2am -->String

		if(hourTime < 23) {
			hourTime = hourTime + 1;
		} else {
			hourTime = 0;
		}


		if (hourTime < 10) {
			hourTimeInString = "0" + hourTime;
		} else {
			hourTimeInString = "" + hourTime;
		}

		String minTime = get2ndNumber(time);
		String endTime = hourTimeInString +":" + minTime;

		return endTime;
	}

	/**
	 * get the HH of the time in hour format(HH:MM)
	 * @param pmTime1
	 * @return HH
	 */
	private static int get1stNumber(String pmTime1) {
		int partOfString1;
		int index = getIndex(pmTime1);
		partOfString1 = Integer.parseInt(pmTime1.substring(0, index));
		return partOfString1;
	}

	/**
	 * get the MM of the time in hour format(HH:MM)
	 * @param pmTime1
	 * @return HH
	 */
	private static String get2ndNumber(String pmTime1) {
		int index = getIndex(pmTime1);
		String partOfString2 = pmTime1.substring(index + 1);
		return partOfString2;
	}

	/**
	 * get the index of the separation of the HH and MM which is either : or .
	 * @param time
	 * @return the index of : or . depend which is detect
	 */
	private static int getIndex(String time) {
		int index = time.indexOf(".");
		if(index == -1){
			index = time.indexOf(":");
		}
		return index;
	}

	/**
	 * piece up the date and time into a string
	 * @param storageOfTime
	 * @param storageOfDate
	 * @return a string contains the day and time.
	 */
	private static String formDateTime(ArrayList<String> storageOfTime,
			ArrayList<String> storageOfDate) {
		String dateTime = "";

		for (int i = 0; i < storageOfDate.size(); i++) {
			dateTime = dateTime + storageOfDate.get(i)+ " "; 
		}

		for (int j = 0; j < storageOfTime.size(); j++) {
			dateTime = dateTime + storageOfTime.get(j) + " ";
		}
		return dateTime;
	}

	/**
	 * get the current date in DD/MM/YYYY
	 * @return date in DD/MM/YYYY
	 */
	private static String getCurrentDate() {
		DateFormat date = new SimpleDateFormat(DATE_FORMAT);
		Calendar cal = Calendar.getInstance();
		String dateOfTheTask = date.format(cal.getTime());
		return dateOfTheTask;
	}

	/**
	 * get the current time in HH:MM
	 * @return time in HH:MM
	 */
	private static String getCurrentTime() {
		DateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);
		Calendar cal = Calendar.getInstance();
		String timeOfTheTask = dateFormat.format(cal.getTime());
		return timeOfTheTask;
	}

	/**
	 * set the number of time stored in arrayList
	 * @param storageOfParameters
	 */
	public static void setNumberOfTime(ArrayList<String> storageOfParameters) {
		numberOfTime = storageOfParameters.size();
	}

	/**
	 * return the number of time detected and stored in arrayList
	 * @return the number of time detected
	 */
	public static int getNumberOfTime() {
		return numberOfTime;
	}
}
