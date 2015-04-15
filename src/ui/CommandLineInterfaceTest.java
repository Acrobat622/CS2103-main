package ui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import logic.LogicController;
import storage.Memory;
public class CommandLineInterfaceTest {

    CommandLineInterface cli = new CommandLineInterface();
    LogicController lc = LogicController.getInstance();
    
   // @Before
    public void setUp() throws Exception {
        // clear all tasks. return to a clean status
        Memory.getInstance().removeAll();
    }

//    @After
//    public void tearDown() throws Exception {
//        Memory.getInstance().removeAll();
//    }

    @Test
    public void testAdd() throws Exception{
        setUp();
        // unknown command and execute add by default
        String feedback = cli.processUserInput("task a");
        System.out.println(feedback);
        assertTrue(feedback.contains("task a"));
        assertTrue(lc.getTaskList().size() == 1);
    
        // task with relative date only
        feedback = cli.processUserInput("a task b from tomorrow to friday");
        System.out.println(feedback);
        assertTrue(feedback.contains("task b"));
        assertTrue(lc.getTaskList().size() == 2);
        assertEquals("17/04/2015", lc.getTaskList().get(0).getEndDate());
    
        // task with no description but time, no task shall be added
        feedback = cli.processUserInput("add from tomorrow to friday");
        System.out.println(feedback);
        assertTrue(feedback.equals("No description for new task\n"));
        assertTrue(lc.getTaskList().size() == 2);
        
        // task with number as description
        feedback = cli.processUserInput("add ssu2000 lesson next friday");
        System.out.println(feedback);
        assertEquals("ssu2000 lesson", lc.getTaskList().get(1).getDescription());
        
        // task with escaped character
        feedback = cli.processUserInput("a meet at bookstore ~Saturday~ by 10pm");
        System.out.println(feedback);
        assertEquals("meet at bookstore Saturday", lc.getTaskList().get(0).getDescription());
    }     

    @Test 
    public void testDelete() {
        // remove a negative number
        String feedback = cli.processUserInput("delete -1");
        assertEquals(4, lc.getTaskList().size());
        assertTrue(feedback.contains("-1"));
        
        // remove a number grater than the size
        feedback = cli.processUserInput("delete 7");
        assertEquals(4, lc.getTaskList().size());
        assertTrue(feedback.contains("7"));
        
        // remove a task by description
        feedback = cli.processUserInput("d ssu2000");
        assertEquals(3, lc.getTaskList().size());
        assertTrue(feedback.contains("ssu2000"));
    }
    
    @Test
    public void testEdit() {
        
    }
}
