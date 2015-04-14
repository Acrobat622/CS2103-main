package parser;

/**
 * get number of times detected, get task type, get start and end time and date,
 * get user input left after extracting all time and date, get description and
 * get feedback on tasks before current date and many other.
 * 
 * @author A0112823R
 *
 */
public class MainParser {
    private String endDate;
    private String endTime;
    private String startTime;
    private String startDate;
    private String feedback;
    private String description;
    private String taskType;
    private String userInputLeft;
    public int numberOfTime;

    public MainParser(String userInput) throws Exception {

        DateTimeParser dateTimeParser = new DateTimeParser(userInput);
        endDate = dateTimeParser.getEndDate();
        startDate = dateTimeParser.getStartDate();
        startTime = dateTimeParser.getStartTime();
        endTime = dateTimeParser.getEndTime();
        feedback = dateTimeParser.getFeedBack();
        numberOfTime = dateTimeParser.getNumberOfTimeDetected();
        userInputLeft = dateTimeParser.getUserInputLeft();

        DescriptionParser descriptionParser = new DescriptionParser(userInput,
                userInputLeft);
        description = descriptionParser.getDescription();

        TaskTypeParser taskTypeParser = new TaskTypeParser(numberOfTime);
        taskType = taskTypeParser.getTaskType();
    }

    /**
     * get the number of time detected
     * 
     * @return number of time detected
     */
    public int getNumberOfTime() {
        return numberOfTime;
    }

    /**
     * get user input left after extracting all time and date detected
     * 
     * @return user input left
     */
    public String getUserInputLeft() {
        return userInputLeft;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getTaskType() {
        return taskType;
    }

    public String getDescription() {
        return description;
    }

    /**
     * get feedback like user keyed in tasks before current date and the start
     * time or date before current date
     * 
     * @return feedback message
     */
    public String getFeedback() {
        return feedback;
    }

}
