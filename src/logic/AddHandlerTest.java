
//@author A0114463M
package logic;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import storage.Memory;
import application.Task;
import application.TaskCreator;
public class AddHandlerTest {

    Memory memory = Memory.getInstance();
	AddHandler ah = new AddHandler();
	ArrayList<Task> taskTest = new ArrayList<Task>();
	ArrayList<Task> expected = new ArrayList<Task>();
	Task task1, task2, task3 = null;
	
	@Before
	public void setUp() throws Exception {
	    memory.removeAll();
	    TaskCreator tc = new TaskCreator("help mom for dinner tomorrow 6 to 7pm");
	    task1 = tc.createNewTask();
	    tc.setNewString("read Harry Porter");
	    task2 = tc.createNewTask();
	    tc.setNewString("Reflectoin by tomorrow 10pm");
	    task3 = tc.createNewTask();
	}

	
}
