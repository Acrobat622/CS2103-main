package logic;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import storage.Memory;
import application.Task;
import application.TaskCreator;

public class EditHandlerTest {
    Memory memory = Memory.getInstance();
    EditHandler eh = new EditHandler();
    Task task1, task2, task3 = null;
    Task newTask1, newTask2, newTask3 = null;
    
    @Before
    public void setUp() throws Exception {
        memory.removeAll();
        TaskCreator tc = new TaskCreator("help mom for dinner tomorrow 6 to 7pm");
        task1 = tc.createNewTask();
        tc.setNewString("help mom for dinner tomorrow 5pm to 6pm");
        newTask1 = tc.createNewTask();
        tc.setNewString("CS2103 reflection by tomorrow 10pm");
        task2 = tc.createNewTask();
        tc.setNewString("CS2101 reflection by tomorrow 10pm");
        newTask2 = tc.createNewTask();
        tc.setNewString("read Harry Porter");
        task3 = tc.createNewTask();
        tc.setNewString("watch Harry Porter");
        newTask3 = tc.createNewTask();
        
        memory.addTask(task1);
        memory.addTask(task2);
        memory.addTask(task3);

    }


    @Test
    /*
     * Boundary case where the new task is empty
     * nothing changes shall made
     */
    public void testEdit1() {   

        ArrayList<Task> taskTest = new ArrayList<Task>();
        ArrayList<Task> expected = new ArrayList<Task>();
        taskTest.add(task1);
        taskTest.add(task2);
        taskTest.add(task3);
        
        expected.add(task1);
        expected.add(task2);
        expected.add(task3);
        
        try {
            eh.execute("edit", "", taskTest);
            assertEquals(taskTest, expected);
        } catch (Exception e) {
            fail();
        }
    }

}
