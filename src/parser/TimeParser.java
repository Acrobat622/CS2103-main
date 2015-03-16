package parser;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class TimeParser {
	private static final String TIME_KEYWORD_1 = "(((\\d+[.:](\\d+|)|\\d+)(-| to | - )(\\d+[.:](\\d+|)|\\d+)(\\s|)(am|pm)))";
	private static final String TIME_KEYWORD_2 = "\\b(on |at |from |to |)(\\d+[.:,]\\d+|\\d+)((\\s|)(am|pm))\\b";
	private static final String TIME_KEYWORD_3 = "\\b(on |at |from |to |)noon | (on |at |from |to |)midnight";
	//private static final String TIME_KEYWORD_4 = "\\b(\\d+[:.]\\d+(\\s|)(-|to|))\\b";
	private static final String TIME_KEYWORD_4 = "(before midnight|before noon)";
	private static final String DEADLINE_KEYWORD = "(due|by)";
	private static final String TO_BE_REMOVED_KEYWORD = "(am|pm|\\s|-|to|at|from)";
	private static final String INVALID_TIME = "Time entered is invalid";
	private static final int TIME_FORMAT_1 = 1;
	private static final int TIME_FORMAT_2 = 2;
	private static final int TIME_FORMAT_3 = 3;
	private static final int TIME_FORMAT_4 = 4;
	private static String detectUserInput;
	private int numberOfTime;


	public static ArrayList<String> extractTime(String userInput) {
		ArrayList<String> storageOfTime = new ArrayList<String>();
		TimeParser.detectUserInput = userInput;
		for(int i = 1; i <= 4; i++){
			storageOfTime = goThroughTimeFormat(i, storageOfTime);
		}
		if(!userInput.contains("due") && !userInput.contains("by")){
			String hourTimeInString;
			if(storageOfTime.size() == 1){
				int hourTime =  get1stNumber(storageOfTime.get(0)); //take note if 2am -->String
				hourTime = hourTime + 1;
				if (hourTime < 10) {
					hourTimeInString = "0" + hourTime;
				} else {
					hourTimeInString = "" + hourTime;
				}
				String minTime = get2ndNumber(storageOfTime.get(0));
				String endTime = hourTimeInString +":" + minTime;
				storageOfTime.add(endTime);
			} 
		}		
		//System.out.println("2 time: "+ storageOfTime.get(0)+" 2: "+storageOfTime.get(1));
		return storageOfTime;
	}

	private void checkIfAllTimeInvalid(ArrayList<String> storageOfTime) {
		int numberOfTimeStored = storageOfTime.size();
		int numberOfInvalidMsg = countNumberOfInvalidMsg(storageOfTime);
		if(isAllTimeKeyedInvalid(numberOfTimeStored, numberOfInvalidMsg)){
			storageOfTime.add(INVALID_TIME);
		}
	}

	private boolean isAllTimeKeyedInvalid(int numberOfTimeStored,
			int numberOfInvalidMsg) {
		return numberOfInvalidMsg == numberOfTimeStored;
	}

	private int countNumberOfInvalidMsg(ArrayList<String> storageOfTime) {
		int numberOfInvalidMsg = 0;
		for(int j = 0; j < storageOfTime.size(); j++){
			if(storageOfTime.get(j).equals(INVALID_TIME)){
				numberOfInvalidMsg++;
				storageOfTime.remove(j);
			}
		}
		return numberOfInvalidMsg;
	}

	private static ArrayList<String> goThroughTimeFormat(int timeFormat, 
			ArrayList<String> storageOfTime) {
		if(timeFormat == TIME_FORMAT_1){
			storageOfTime = detectUsingFormat1(storageOfTime);
		}
		else if(timeFormat == TIME_FORMAT_2){
			storageOfTime = detectUsingFormat2(storageOfTime);
		}
		else if(timeFormat == TIME_FORMAT_3){
			storageOfTime = detectUsingFormat3(storageOfTime);
		}
		else if(timeFormat == TIME_FORMAT_4){
			storageOfTime = detectUsingFormat4(storageOfTime);
		}
		return storageOfTime;
	}

	/**
	 * detect start time and end time. 
	 * detect HH:MM/12hour format pm/am/none to/- HH:MM/12hour format pm/am
	 * example 12:30 - 1pm/12pm to 1:30pm
	 * @param storageOfTime
	 * @return arrayList containing all of the time.
	 */
	private static ArrayList<String> detectUsingFormat1(ArrayList<String> storageOfTime) {
		Pattern timeDetector = Pattern.compile(TIME_KEYWORD_1);
		Matcher matchedWithTime = timeDetector.matcher(detectUserInput);
		String[] timeList;
		String toBeAdded = "";

		while (matchedWithTime.find()) {
			String time = matchedWithTime.group();
			detectUserInput = detectUserInput.replaceAll(time, "");
			timeList = time.split("-|to");

			System.out.println("1time: "+time);
			timeList[1] = changeToHourFormat(timeList[1]);
			if(timeList[0].contains("am") || timeList[0].contains("pm")){
				timeList[0] = changeToHourFormat(timeList[0]);
			} else {
				String amTime1 = changeToHourFormat(timeList[0]+"am");
				String pmTime1 = changeToHourFormat(timeList[0]+"pm");
				//	if(!checkValid24HourTime(amTime1) && !checkValid24HourTime(pmTime1)){
				//	storageOfTime.add(INVALID_TIME);
				//	}
				int amTime1stNum = get1stNumber(amTime1);
				int pmTime1stNum = get1stNumber(pmTime1);
				int time1stNum = get1stNumber(timeList[1]);

				toBeAdded = detectWhichOneIsRight(toBeAdded, amTime1, pmTime1,
						amTime1stNum, pmTime1stNum, time1stNum);
				storageOfTime.add(toBeAdded);	
			}
			storageOfTime.add(timeList[1]);
		}
		return storageOfTime;
	}

	/**
	 * detect the start time when pm/am is not added right next to start time. 
	 * @param toBeAdded
	 * @param amTime1
	 * @param pmTime1
	 * @param amTime1stNum
	 * @param pmTime1stNum
	 * @param time1stNum
	 * @return the right timing when switch to hour format when am or pm is not on the back of the time.
	 */
	private static String detectWhichOneIsRight(String toBeAdded, String amTime1,
			String pmTime1, int amTime1stNum, int pmTime1stNum, int time1stNum) {
		if(amTime1stNum < time1stNum && pmTime1stNum < time1stNum){
			toBeAdded = whenBothLessThan(amTime1, pmTime1, amTime1stNum,
					pmTime1stNum);
		}
		else if(amTime1stNum < time1stNum){
			toBeAdded = amTime1;
		}
		else if(pmTime1stNum < time1stNum){
			toBeAdded = pmTime1;
		}
		else if(amTime1stNum == pmTime1stNum){
			toBeAdded = pmTime1;
		}
		return toBeAdded;
	}

	/**when both start time hour format is less than the end time.
	 * 
	 * @param amTime1
	 * @param pmTime1
	 * @param amTime1stNum
	 * @param pmTime1stNum
	 * @return the right start time when the am/pm is not input. 
	 */
	private static String whenBothLessThan(String amTime1, String pmTime1,
			int amTime1stNum, int pmTime1stNum) {
		String toBeAdded;
		if(amTime1stNum < pmTime1stNum){
			toBeAdded = pmTime1;
		}
		else{
			toBeAdded = amTime1;
		}
		return toBeAdded;
	}

	/**
	 * get the HH of the time in hour format(HH:MM)
	 * @param pmTime1
	 * @return HH
	 */
	private static int get1stNumber(String pmTime1) {
		int index = getIndex(pmTime1);
		int partOfString1 = Integer.parseInt(pmTime1.substring(0, index));
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
	/*
	private ArrayList<String> detectUsingFormat4(ArrayList<String> storageOfTime) {
		Pattern timeDetector = Pattern.compile(TIME_KEYWORD_4);
		Matcher matchedWithTime = timeDetector.matcher(userInput);

		while (matchedWithTime.find()) {
			String time = matchedWithTime.group();
			if(checkValid24HourTime(time)){
				userInput = userInput.replaceAll(time, "");
				time = removeUnwantedParts(time);
				//System.out.println("24Htime: "+time);
				int index = getIndex(time);
				time = time.substring(0, index) + ":" + time.substring(index+1);
				storageOfTime.add(time);
			} 
			else{
				storageOfTime.add(INVALID_TIME);
			}
		}

		return storageOfTime;
	}
	 */
	private boolean checkValid24HourTime(String time) {
		time = removeUnwantedParts(time);
		boolean validTime = false;
		if(time.length() > 2){
			int index = getIndex(time);
			int partOfString1 = Integer.parseInt(time.substring(0, index));
			int partOfString2 = Integer.parseInt(time.substring(index + 1));
			if(partOfString1 < 24 && partOfString1 >= 0
					&& partOfString2 >= 0 && partOfString2 <=59){
				validTime = true;
			}
		}
		return validTime;
	}

	/**
	 * detect HH:MM/HH with pm and am behind.
	 * @param storageOfTime
	 * @return storage of time containing the time detected.
	 */
	private static ArrayList<String> detectUsingFormat2(ArrayList<String> storageOfTime) {
		Pattern timeDetector = 
				Pattern.compile(TIME_KEYWORD_2);
		Matcher matchedWithTime = timeDetector.matcher(detectUserInput);

		while (matchedWithTime.find()) {
			String time = matchedWithTime.group();
			if(checkValid12HourTime(time)){
				detectUserInput = detectUserInput.replaceAll(time, "");
				//System.out.println("1. beforeTime: "+time);
				time = changeToHourFormat(time);
				//System.out.println("1. afterTime: "+time);
				storageOfTime.add(time);
			} 
			else{
				storageOfTime.add(INVALID_TIME);
			}
		}

		return storageOfTime;

	}
	/**
	 * detect noon, midnight
	 * @param storageOfTime
	 * @return storage of time containing the time detected.
	 */
	private static ArrayList<String> detectUsingFormat3(ArrayList<String> storageOfTime) {
		Pattern timeDetector = 
				Pattern.compile(TIME_KEYWORD_3);
		Matcher matchedWithTime = timeDetector.matcher(detectUserInput);

		while (matchedWithTime.find()) {
			detectUserInput = detectUserInput.replaceAll(TIME_KEYWORD_3, "");
			String time = matchedWithTime.group();
			if(time.contains("noon")){
				storageOfTime.add("12:00");
			} else if(time.contains("midnight")){
				storageOfTime.add("00:00");
			}
		}  
		//System.out.println("2.timeDe: "+storageOfTime.get(0));
		return storageOfTime;
	}

	/**
	 * detect before noon and before midnight 
	 * @param storageOfTime
	 * @return the time in hour format.
	 */
	private static ArrayList<String> detectUsingFormat4(ArrayList<String> storageOfTime) {
		Pattern timeDetector = 
				Pattern.compile(TIME_KEYWORD_4);
		Matcher matchedWithTime = timeDetector.matcher(detectUserInput);

		while (matchedWithTime.find()) {
			detectUserInput = detectUserInput.replaceAll(TIME_KEYWORD_4, "");
			String time = matchedWithTime.group();
			if(time.contains("noon")){
				storageOfTime.add("11:59");
			} else if(time.contains("midnight")){
				storageOfTime.add("23:59");
			}
		}  
		//System.out.println("2.timeDe: "+storageOfTime.get(0));
		return storageOfTime;
	}

	private static boolean checkValid12HourTime(String time) {
		boolean validTime = false;
		String amOrPM = getAmOrPm(time);
		time = removeUnwantedParts(time);

		if(time.length() > 2){
			int index = getIndex(time);
			int partOfString1 = Integer.parseInt(time.substring(0, index));
			int partOfString2 = Integer.parseInt(time.substring(index + 1));
			if(partOfString1 < 24 && (partOfString1 > 0 || (amOrPM.equals("am") && partOfString1 == 0))
					&& partOfString2 >= 0 && partOfString2 <=59){
				validTime = true;
			}
		}
		else{
			int partOfString = Integer.parseInt(time);
			if(partOfString < 13 && partOfString > 0){
				validTime = true;
			}		
		}
		return validTime;
	}

	/**
	 * detect the time contain am or pm
	 * @param time
	 * @return am or pm depend which one is detected.
	 */
	private static String getAmOrPm(String time) {	
		String amOrPm = "";
		if(time.contains("am")){
			amOrPm = "am";
		}
		else if(time.contains("pm")){
			amOrPm = "pm";
		}
		return amOrPm;
	}

	/**
	 * change all of the time inputed to hour format(HH:MM)
	 * @param time
	 * @return time in hour format (HH:MM)
	 */
	private static String changeToHourFormat(String time) {
		if(time.contains("am")){
			time = removeUnwantedParts(time);
			int index = getIndex(time);	
			if(time.length() > 2 && time.charAt(0) == '1' && time.charAt(1) == '2'){
				time = "00" + ":" + time.substring(index+1);
			}
			else if(time.length() == 2 && time.charAt(0) == '1' && time.charAt(1) == '2'){
				time = "00:00";
			}
			else if(time.length() <= 2){
				time = time +":00";
			}
			else if(time.length() > 2){
				time = time.substring(0, index) + ":" + time.substring(index+1);
			}
		}
		else if(time.contains("pm")){
			time = removeUnwantedParts(time);
			if(time.length() <= 2 && time.length() > 0){
				time = switchToPMHour(time) + ":00";
			}
			else if(time.length() > 2){
				int index = getIndex(time);		
				time = switchToPMHour(time.substring(0, index)) + ":" + time.substring(index+1);
			}		
		}
		return time;
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
	 * if pm is detected behind the time, switch it to hour format from hour at 13 onwards to 00
	 * @param time
	 * @return hour format for time in pm.
	 */
	private static String switchToPMHour(String time) {
		int timeHour = 13, timeNormal = 1;

		while(timeHour != 24){
			if(time.length() == 2 && time.charAt(0) == '1' && time.charAt(1) == '2'){
				time = "12";
				break;
			}
			if(time.charAt(0) == (char)(timeNormal + 48)){
				time = String.valueOf(timeHour);
				break;
			}
			timeHour++;
			timeNormal++;
		}
		return time;		
	}

	/**
	 * remove the conjunction
	 * @param timeWithUnwantedPart
	 * @return time free from conjunction
	 */
	public static String removeUnwantedParts(String timeWithUnwantedPart) {
		String time;
		time  = timeWithUnwantedPart.replaceAll(TO_BE_REMOVED_KEYWORD, "");
		return time;
	}

	public static int getNumberOfTime() {

		return 0;
	}
}
