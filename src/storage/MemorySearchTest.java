package storage;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;


import java.util.ArrayList;
import java.text.ParseException;

import application.Task;
import application.TaskCreator;
import application.TaskComparator;
/**
 * This test class only tests the search date and search time function
 * of the memory class. some sample tasks are defined and memory is cleared for
 * testing before running the test cases
 * @author A0114463M
 *
 */
public class MemorySearchTest {
    Memory memory = Memory.getInstance();
    Task newTask1, newTask2, newTask3, newTask4, newTask5, newTask6, newTask7;    
    @Before
    public void setUp() throws Exception {
          TaskCreator tc = new TaskCreator("task 1 from 01/05/2015 5pm to 6pm");
          newTask1 = tc.createNewTask();
          tc.setNewString("task 2 01/05/2015 by 10pm");
          newTask2 = tc.createNewTask();
          tc.setNewString("task 3 02/05/2015 from 4am to 5am");
          newTask3 = tc.createNewTask();
          tc.setNewString("task 4 from 01/05/2015 6pm to 8pm");
          newTask4 = tc.createNewTask();
          tc.setNewString("task 5 from 25/04 to 29/04");
          newTask5 = tc.createNewTask();
          tc.setNewString("task 6 from 20/04 to 23/04");
          newTask6 = tc.createNewTask();
          tc.setNewString("task 7 02/05/2015 1am");
          newTask7 = tc.createNewTask();
          
          memory.removeAll();
          memory.addTask(newTask1);
          memory.addTask(newTask2);
          memory.addTask(newTask3);
          memory.addTask(newTask4);
          memory.addTask(newTask5);
          memory.addTask(newTask6);
          memory.addTask(newTask7);
          
    }

    /*
     * The following test cases overs only the date related searches
     */
    @Test
    /**
     * this is a boundary case where the date specified is exactly same as the dates of some tasks
     */
    public void testSearchSingleDay1() {        
        ArrayList<Task> correctSearchList = new ArrayList<Task>();
        correctSearchList.add(newTask1);
        correctSearchList.add(newTask2);
        correctSearchList.add(newTask4);
        Collections.sort(correctSearchList, new TaskComparator());
        try {
             assertEquals(correctSearchList, memory.searchDate("01/05/2015"));
        } catch (ParseException pe) {
            System.out.println("Error while parsing date\n");
        }
    }

    @Test
    /**
     * this is boundary cases where the date specified is right before or after the date
     */
    public void testSearchSingleDay2() {
        try {
             assertTrue(memory.searchDate("03/05/2015").isEmpty());
             assertTrue(memory.searchDate("30/04/2015").isEmpty());
        } catch (ParseException pe) {
            System.out.println("Error while parsing date\n");
        }
    }
    
    @Test
    /**
     * This is a boundary case when the end of the period specified matches the start
     * of some dates
     */
    public void testSearchPeriodDays1() {
        ArrayList<Task> correctSearchList = new ArrayList<Task>();
        correctSearchList.add(newTask1);
        correctSearchList.add(newTask2);
        correctSearchList.add(newTask4);
        Collections.sort(correctSearchList, new TaskComparator());
        try {
            assertEquals(correctSearchList, memory.searchDate("30/04/2015", "01/05/2015"));
       } catch (ParseException pe) {
           System.out.println("Error while parsing date\n");
       }
    }
    
    @Test
    /**
     * This is a boundary case when the start of the period specified matches the end
     * of some dates
     */
    public void testSearchPeriodDays2() {
        ArrayList<Task> correctSearchList = new ArrayList<Task>();
        correctSearchList.add(newTask7);
        correctSearchList.add(newTask3);
        try {
            assertEquals(correctSearchList, memory.searchDate("02/05/2015", "04/05/2015"));
       } catch (ParseException pe) {
           System.out.println("Error while parsing date\n");
       }
    }
    
    @Test
    /**
     * This is a boundary case when period specified covers the entire time span
     * of some tasks
     */
    public void testSearchPeriodDays3() {
        ArrayList<Task> correctSearchList = new ArrayList<Task>();
        correctSearchList.add(newTask5);
        try {
            assertEquals(correctSearchList, memory.searchDate("24/04/2015", "30/04/2015"));
       } catch (ParseException pe) {
           System.out.println("Error while parsing date\n");
       }
    }
    
    @Test
    /**
     * This is a boundary case when period specified falls within the entire time span
     * of some tasks
     */
    public void testSearchPeriodDays4() {
        ArrayList<Task> correctSearchList = new ArrayList<Task>();
        correctSearchList.add(newTask5);
        try {
            assertEquals(correctSearchList, memory.searchDate("26/04/2015", "27/04/2015"));
       } catch (ParseException pe) {
           System.out.println("Error while parsing date\n");
       }
    }
    
    @Test
    /**
     * This is a boundary case when period specified only covers portion 
     * of some tasks
     */
    public void testSearchPeriodDays5() {
        ArrayList<Task> correctSearchList = new ArrayList<Task>();
        correctSearchList.add(newTask6);
        correctSearchList.add(newTask5);
        try {
            assertEquals(correctSearchList, memory.searchDate("22/04/2015", "26/04/2015"));
       } catch (ParseException pe) {
           System.out.println("Error while parsing date\n");
       }
    }
    
    /*
     * The following test cases covers the time related searches
     */
    @Test
    /**
     * this is a boundary case where the date specified is exactly same as the time of 
     * start or end of some tasks
     */
    public void testSearchSingleTime1() {        
        ArrayList<Task> correctSearchList = new ArrayList<Task>();
        correctSearchList.add(newTask3);
        Collections.sort(correctSearchList, new TaskComparator());
        try {
             assertEquals(correctSearchList, memory.searchTime("02/05/2015 04:00"));
             assertEquals(correctSearchList, memory.searchTime("02/05/2015 05:00"));
        } catch (ParseException pe) {
            System.out.println("Error while parsing date\n");
        }
        
        correctSearchList.clear();
        correctSearchList.add(newTask1);
        correctSearchList.add(newTask4);
        Collections.sort(correctSearchList, new TaskComparator());
        try {
            assertEquals(correctSearchList, memory.searchTime("01/05/2015 18:00"));
       } catch (ParseException pe) {
           System.out.println("Error while parsing date\n");
       }
    }
    
    @Test
    /**
     * this is a boundary case where the date specified is slightly before or after
     *  the time of start or end of some tasks
     */
    public void testSearchSingleTime2() {        
        try {
             assertTrue(memory.searchTime("02/05/2015 03:59").isEmpty());
             assertTrue(memory.searchTime("02/05/2015 05:01").isEmpty());
        } catch (ParseException pe) {
            System.out.println("Error while parsing date\n");
        }
    }
    
    @Test
    /**
     * this is a boundary case where the date specified is within one hour of
     * the deadline of a task
     */
    public void testSearchSingleTime3() { 
        ArrayList<Task> correctSearchList = new ArrayList<Task>();
        correctSearchList.add(newTask7);
        try {
             assertEquals(correctSearchList, memory.searchTime("02/05/2015 00:30"));
             assertEquals(correctSearchList, memory.searchTime("02/05/2015 00:00"));
             assertTrue(memory.searchTime("01/05/2015 23:59").isEmpty());
        } catch (ParseException pe) {
            System.out.println("Error while parsing date\n");
        }
    }
    
    @After
    public void resetMemory() {
        memory.removeAll();
    }
}
