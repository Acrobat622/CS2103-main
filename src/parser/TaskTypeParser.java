package parser;

/**
 * get the task type for the user input
 * 
 * @author A0112823R
 *
 */
public class TaskTypeParser {
    private String taskType;

    public TaskTypeParser(int numberOfTimeInput) throws Exception {
        String taskType = null;

        if (numberOfTimeInput == 2) {
            taskType = "time task";
        } else if (numberOfTimeInput == 1) {
            taskType = "deadline";
        } else if (numberOfTimeInput == 0) {
            taskType = "floating task";
        }

        assert taskType != null : "error in detection detect too many time! or user key more than 2 times";

        setTaskType(taskType);
    }

    public void setTaskType(String detectedTaskType) {
        taskType = detectedTaskType;
    }

    public String getTaskType() {
        return taskType;
    }
}
