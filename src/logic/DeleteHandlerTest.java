
//@author A0114463M

package logic;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import storage.Memory;

import java.util.*;

import application.Task;
import application.TaskCreator;
public class DeleteHandlerTest {

	DeleteHandler dh = new DeleteHandler();
	ArrayList<Task> taskTest = new ArrayList<Task>();
	ArrayList<Task> expected = new ArrayList<Task>();
	Task task1, task2, task3;
	 
	@Before
	public void setUp() throws Exception {
	    Memory.getInstance().removeAll();
	    TaskCreator  tc = new TaskCreator("CS2103 exam tomorrow 10am to 12pm");
	    task1 = tc.createNewTask();
	    tc.setNewString("read Harry Porter");
	    task2 = tc.createNewTask();
	    tc.setNewString("CG2271 exam tomorrow 3pm to 5pm");
	    task3 = tc.createNewTask();
	}
		
	
	public void  setUp1() {		
	    try {
	        setUp();
	    } catch (Exception e) {
	        
	    }
		expected.clear();
		taskTest.clear();
		taskTest.add(task1);
		taskTest.add(task2);
	}

	
	/*Test1
	 * This is a boundary case where user deletes multiple tasks in 
	 * random order
	 */
	@Test
	public void testExecute1() {
		setUp1();
		try {
            dh.execute("d", "2 1", taskTest);
        } catch (Exception e) {
            e.printStackTrace();
        }
		assertEquals(taskTest, expected);
	}
	
	public void setUp2() {
		expected.clear();
		taskTest.clear();
		taskTest.add(task1);
		taskTest.add(task2);
		taskTest.add(task3);
		expected.add(task1);
		expected.add(task2);
		expected.add(task3);
	}
	
	/*Test2
     *
	 * This is a boundary case when user types a non-positive number(negative partition?) 
	 */
	@Test
	public void testExecute2_1() {
		setUp2();
		try {
            dh.execute("d", "-1", taskTest);
        } catch (Exception e) {
            e.printStackTrace();
        }
		assertEquals(taskTest, expected);
	}
	/*Test2
    *
    * This is a boundary case when user types zero(negative partition?) 
    */
	@Test
	public void testExecute2_2() {
        setUp2();
        try {
            dh.execute("d", "0", taskTest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(taskTest, expected);
    }
	
	public void setUp3() {
		expected.clear();
		taskTest.clear();
		taskTest.add(task1);
		taskTest.add(task2);
		taskTest.add(task3);
		expected.add(task1);
		expected.add(task2);
		expected.add(task3);
	}

	/*Test 3
	 * This is a boundary case when user types keyword while no tasks
	 * is containing this letter 
	 */
	@Test
	public void testExecute3() {
		setUp3();
		try {
            dh.execute("DeLEte", "final", taskTest);
        } catch (Exception e) {
            e.printStackTrace();
        }
		assertEquals(taskTest, expected);
	}
	
	public void setUp4() {
		expected.clear();
		taskTest.clear();
		taskTest.add(task2);
		taskTest.add(task3);
		expected.add(task2);
		expected.add(task3);
	}
	/*Test 4
	 * This is a boundary case when user types an index which is larger than
	 * the size (size boundary partition?)
	 */
	@Test
	public void testExecute4() {
		setUp4();
		try {
            dh.execute("D", "4", taskTest);
        } catch (Exception e) {
            e.printStackTrace();
        }
		assertEquals(taskTest, expected);
	}
	
	public void setUp5() {
        expected.clear();
        taskTest.clear();
        taskTest.add(task1);
        taskTest.add(task2);
        taskTest.add(task3);
        expected.add(task2);
        expected.add(task3);
    }
	/*Test 4
     * This is a boundary case when user types description of task
     * where there are multiple tasks containing this keyword
     */
    @Test
    public void testExecute5() {
        setUp4();
        try {
            dh.execute("D", "exam", taskTest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(taskTest, expected);
    }
}
