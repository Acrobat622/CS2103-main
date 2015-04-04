package parser;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * take note that most of the test case is sensitive to the current date or current time
 * Pls set them according to your laptop's time and date
 * For example, usage of today and adding of current time when 1 today is detected.
 * @author WeiLin
 *
 */
public class DateTimeParserTest {

    //test the addition of time when the user miss out
    @Test
    public void testWhenTimeNotKeyed() {
        // deadline task without time added and have 1 date
        DateTimeParser d1 = new DateTimeParser(
                "add start reading ST2334 notes by this sat");
        assertEquals(d1.getStartDate(), "Sat 04/04/2015");
        assertEquals(d1.getStartTime(), "23:59");

        // deadline task without time and 1 date --> today
        DateTimeParser d4 = new DateTimeParser(
                "add start reading ST2334 notes by Friday");
        assertEquals(d4.getStartDate(), "Fri 03/04/2015");
        assertEquals(d4.getStartTime(), "23:59");

        // deadline task without time and 1 date
        DateTimeParser d6 = new DateTimeParser(
                "add start reading ST2334 notes next Friday");
        assertEquals(d6.getStartDate(), "Fri 10/04/2015");
        assertEquals(d6.getStartTime(), "23:59");

        // timed task with only one time added and 2 dates added
        DateTimeParser d2 = new DateTimeParser(
                "CS2103T assignments 23/2/2016 24/3/2016 4pm");
        assertEquals(d2.getStartDate(), "Tues 23/02/2016");
        assertEquals(d2.getEndDate(), "Thur 24/03/2016");
        assertEquals(d2.getStartTime(), "16:00");
        assertEquals(d2.getEndTime(), "23:59");

        // timed task with no time added and 2 dates
        DateTimeParser d3 = new DateTimeParser(
                "CS2103T assignments 23/2/2016 24/3/2016");
        assertEquals(d3.getStartDate(), "Tues 23/02/2016");
        assertEquals(d3.getEndDate(), "Thur 24/03/2016");
        assertEquals(d3.getStartTime(), "00:00");
        assertEquals(d3.getEndTime(), "23:59");

        // timed task with no time added and 2 dates(today, tomorrow)
        DateTimeParser d5 = new DateTimeParser(
                "CS2103T assignments today tomorrow");
        assertEquals(d5.getStartDate(), "Fri 03/04/2015");
        assertEquals(d5.getEndDate(), "Sat 04/04/2015");
        // current time is set for start time pls ensure that it is set to ur
        // com current time

        // assertEquals(d5.getStartTime(), "19:55");
        assertEquals(d5.getEndTime(), "23:59");

        // timed task with no time added and 2 dates(today, today)
        DateTimeParser d7 = new DateTimeParser(
                "CS2103T assignments today 03/04");
        assertEquals(d7.getStartDate(), "Fri 03/04/2015");
        assertEquals(d7.getEndDate(), "Fri 03/04/2015");
        // current time is set for start time pls ensure that it is set to ur
        // com current time
        // assertEquals(d7.getStartTime(), "19:53");
        assertEquals(d7.getEndTime(), "23:59");

        // timed task with 1 times and 2 dates (yesterday and today)
        DateTimeParser d8 = new DateTimeParser(
                "CS2103T assignments 02/04/2015 03/04 2pm");
        assertEquals(d8.getStartDate(), "Thur 02/04/2015");
        assertEquals(d8.getEndDate(), "Fri 03/04/2015");
        assertEquals(d8.getStartTime(), "14:00");
        assertEquals(d8.getEndTime(), "23:59");

        // timed task with 1 time added and 2 dates(today, today)
        DateTimeParser d9 = new DateTimeParser(
                "CS2103T assignments today 03/04 noon");
        assertEquals(d9.getStartDate(), "Fri 03/04/2015");
        assertEquals(d9.getEndDate(), "Fri 03/04/2015");
        assertEquals(d9.getStartTime(), "12:00");
        assertEquals(d9.getEndTime(), "23:59");
        
        // timed task with 1 time added and 2 dates(random date past current date)
        DateTimeParser d10 = new DateTimeParser(
                "CS2103T assignments 05 sep and 24 dec midnight");
        assertEquals(d10.getStartDate(), "Sat 05/09/2015");
        assertEquals(d10.getEndDate(), "Thur 24/12/2015");
        assertEquals(d10.getStartTime(), "00:00");
        assertEquals(d10.getEndTime(), "23:59");
    }

    @Test
    // take note that if date is not added and there is time, I set the default
    // to current date
    // When testing, do change the expected result's date to current date.
    // and if the time pass the current time, it will the next day
    public void testWhenDateNotKeyed() {
        // deadline task without date added and past current time : 3pm
        DateTimeParser d1 = new DateTimeParser("CS2103T assignments due 10pm");
        assertEquals(d1.getStartDate(), "Fri 03/04/2015");
        assertEquals(d1.getStartTime(), "22:00");
        
        // deadline task without date added and before current time : 3pm
        DateTimeParser d6 = new DateTimeParser("CS2103T assignments due 2pm");
        assertEquals(d6.getStartDate(), "Sat 04/04/2015");
        assertEquals(d6.getStartTime(), "14:00");

        // timed task with one date added start time is later than end time
        DateTimeParser d2 = new DateTimeParser(
                "CS2103T assignments 5pm to 4pm 23/06/2016");
        assertEquals(d2.getStartDate(), "Thur 23/06/2016");
        assertEquals(d2.getEndDate(), "Fri 24/06/2016");
        assertEquals(d2.getStartTime(), "17:00");
        assertEquals(d2.getEndTime(), "16:00");

        // timed task with one date added start time is later than end time
        DateTimeParser d10 = new DateTimeParser(
                "CS2103T assignments 1pm to 7pm 23/06/2016");
        assertEquals(d10.getStartDate(), "Thur 23/06/2016");
        assertEquals(d10.getEndDate(), "Thur 23/06/2016");
        assertEquals(d10.getStartTime(), "13:00");
        assertEquals(d10.getEndTime(), "19:00");
        
     // timed task with one date (today) added and 2 times past current time:3pm
        DateTimeParser d11 = new DateTimeParser(
                "CS2103T assignments 9pm to 10pm today");
        assertEquals(d11.getStartDate(), "Fri 03/04/2015");
        assertEquals(d11.getEndDate(), "Fri 03/04/2015");
        assertEquals(d11.getStartTime(), "21:00");
        assertEquals(d11.getEndTime(), "22:00");
        
     // timed task with one date (today) added and 2 times past current time:3pm with start>end time
        DateTimeParser d12 = new DateTimeParser(
                "CS2103T assignments 10pm to 9pm today");
        assertEquals(d12.getStartDate(), "Fri 03/04/2015");
        assertEquals(d12.getEndDate(), "Sat 04/04/2015");
        assertEquals(d12.getStartTime(), "22:00");
        assertEquals(d12.getEndTime(), "21:00");

        
        // timed task with no date added and start time is not later than end
        // time
        DateTimeParser d3 = new DateTimeParser("CS2103T assignments 4pm 6:30pm");
        assertEquals(d3.getStartDate(), "Sat 04/04/2015");
        assertEquals(d3.getEndDate(), "Sat 04/04/2015");
        assertEquals(d3.getStartTime(), "16:00");
        assertEquals(d3.getEndTime(), "18:30");

        // timed task with no date added and start time is later than end time
        DateTimeParser d4 = new DateTimeParser("CS2103T assignments 9pm 6:45pm");
        assertEquals(d4.getStartDate(), "Sat 04/04/2015");
        assertEquals(d4.getEndDate(), "Sun 05/04/2015");
        assertEquals(d4.getStartTime(), "21:00");
        assertEquals(d4.getEndTime(), "18:45");

        // timed task with no date added and end time before current time : 3pm
        DateTimeParser d5 = new DateTimeParser("CS2103T assignments 5pm 1pm");
        assertEquals(d5.getStartDate(), "Sat 04/04/2015");
        assertEquals(d5.getEndDate(), "Sun 05/04/2015");
        assertEquals(d5.getStartTime(), "17:00");
        assertEquals(d5.getEndTime(), "13:00");
        
        // timed task with no date added and past current time :3pm
        DateTimeParser d7 = new DateTimeParser("CS2103T assignments 10pm 11:10pm");
        assertEquals(d7.getStartDate(), "Fri 03/04/2015");
        assertEquals(d7.getEndDate(), "Fri 03/04/2015");
        assertEquals(d7.getStartTime(), "22:00");
        assertEquals(d7.getEndTime(), "23:10");
        
    
    }

    @Test
    public void testWhenSameTime() {
        // test when same time is detected with no date detected
        DateTimeParser d3 = new DateTimeParser(
                "CS2103T assignments 6:30pm 6:30pm");
        assertEquals(d3.getStartDate(), "Fri 03/04/2015");
        assertEquals(d3.getEndDate(), "Sat 04/04/2015");
        assertEquals(d3.getStartTime(), "18:30");
        assertEquals(d3.getEndTime(), "18:30");

        // test when same time is detected with 1 date detected
        DateTimeParser d4 = new DateTimeParser("25/4 6:30pm 6:30pm assignments");
        assertEquals(d4.getStartDate(), "Sat 25/04/2015");
        assertEquals(d4.getEndDate(), "Sun 26/04/2015");
        assertEquals(d4.getStartTime(), "18:30");
        assertEquals(d4.getEndTime(), "18:30");

    }

    @Test
    public void normalTest() {
        // deadlines test
        DateTimeParser d1 = new DateTimeParser(
                "CS2103T assignments due 23/5/2015 2pm");
        assertEquals(d1.getStartDate(), "Sat 23/05/2015");
        assertEquals(d1.getStartTime(), "14:00");

        // timed test
        DateTimeParser d2 = new DateTimeParser(
                "CS2103T exam on 24/5 27/09/2015 from 2pm to 4:30pm.");
        assertEquals(d2.getStartTime(), "14:00");
        assertEquals(d2.getEndTime(), "16:30");
        assertEquals(d2.getEndDate(), "Sun 27/09/2015");
        assertEquals(d2.getStartDate(), "Sun 24/05/2015");

        //floating test
        DateTimeParser d3 = new DateTimeParser(
                "happy!!!!!!!!!!");
        assertEquals(d3.getStartTime(), "-");
        assertEquals(d3.getEndTime(), "-");
        assertEquals(d3.getEndDate(), "-");
        assertEquals(d3.getStartDate(), "-");
    }

    @Test
    /**
     * test the remaindar pop-up message
     * Pls be remind that this test case is sensitive to the current time of your computer
     * set it according 
     */
    public void testForRemaindar() {
        // today --> start time before current time but one end time past.
        DateTimeParser d4 = new DateTimeParser("add 03 April 4pm 8pm");
        assertEquals(d4.getStartDate(), "Fri 03/04/2015");
        assertEquals(d4.getEndDate(), "Fri 03/04/2015");
        assertEquals(d4.getStartTime(), "16:00");
        assertEquals(d4.getEndTime(), "20:00");

        // today --> start time and end time before current time
        DateTimeParser d5 = new DateTimeParser("add 03 April 6pm 5pm");
        assertEquals(d5.getStartDate(), "Fri 03/04/2015");
        assertEquals(d5.getEndDate(), "Sat 04/04/2015");
        assertEquals(d5.getStartTime(), "18:00");
        assertEquals(d5.getEndTime(), "17:00");

        // yesterday, today
        DateTimeParser d6 = new DateTimeParser("02/04 today dasdh");
        assertEquals(d6.getStartDate(), "Thur 02/04/2015");
        assertEquals(d6.getEndDate(), "Fri 03/04/2015");
        assertEquals(d6.getStartTime(), "23:59");
        assertEquals(d6.getEndTime(), "23:59");

    }
    
    
    
    /*
     * test for error like : 1) start time > end time 2) start date > end date
     */
    /*
    public void testError(){
        //should pop up error window in gui
        // test for 2) start date > end date
        DateTimeParser d10 = new DateTimeParser(
                "CS2103T assignments 05 sep and 24 aug noon");
       
        
        // test for 1) start time > end time
        DateTimeParser d11 = new DateTimeParser(
                "CS2103T assignments today 7pm");
        
        
    }
*/
}
