package org.ayusoftware.log4jenhanced.tests;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.ayusoftware.log4jenhanced.logging.MemAppender;
import org.ayusoftware.log4jenhanced.logging.VelocityLayout;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * MemAppenderTest tests the MemAppender class
 */
public class MemAppenderTest {

    /**
     * Create a default memory appender using an ArrayList and a VelocityLayout
     * @return the memory appender
     */
    private static MemAppender getDefaultArrayListMemAppenderWithLayout(){

        MemAppender memoryAppender = MemAppender.getInstance(new ArrayList<>());
        memoryAppender.setLayout(new VelocityLayout("[$p] $c $d: $m$n"));       // layout will be [Priority] Category Date: Message newline

        return memoryAppender;
    }

    /**
     * Before each test, the instance of MemAppender is disposed making sure each test has a fresh instance
     */
    @BeforeEach
    public void setUp(){
        // before each method, the instance of MemAppender is disposed so each test has a fresh instance
        // not running in parallel
        MemAppender.dispose();
    }

    /**
     * Testing if the total number of logs is correct
     */
    @Test
    public void givenMemoryAppender_whenLogging_thenLogCountIsCorrect() {

        // Arrange: create a memory appender and a logger, then adds appender to logger
        MemAppender memoryAppender = getDefaultArrayListMemAppenderWithLayout();
        Logger logger = Logger.getLogger(MemAppender.class);
        logger.addAppender(memoryAppender);

        // Act: log two messages with the priority info
        logger.info("hello world 1");
        logger.info("hello world 2");

        // Assert: check if the log count is correct
        Assertions.assertEquals(2, memoryAppender.getCurrentLogs().size(), "The log count must be equals to 2, but was " + memoryAppender.getCurrentLogs().size());
    }

    /**
     * Testing if the log messages are correct
     */
    @Test
    public void givenMemoryAppender_whenLogging_thenLogMessagesAreCorrect(){

        // Arrange: create a memory appender and a logger, then adds appender to logger
        MemAppender memoryAppender = getDefaultArrayListMemAppenderWithLayout();
        Logger logger = Logger.getLogger(MemAppender.class);
        logger.addAppender(memoryAppender);

        // Act: log two messages with the priority info
        logger.info("This is test 1");
        logger.info("This is test 2");

        List<String> logMessages = new ArrayList<>();
        for (LoggingEvent event : memoryAppender.getCurrentLogs()) {
            logMessages.add(event.getRenderedMessage());
        }

        // Assert: check if the log messages are correct
        Assertions.assertTrue(logMessages.contains("This is test 1"), "The log messages must contain 'This is test 1', but was " + logMessages);
        Assertions.assertTrue(logMessages.contains("This is test 2"), "The log messages must contain 'This is test 2', but was " + logMessages);
    }

    /**
     * Testing that MemAppender correctly logs messages to console and check that the output contains the logged message
     */
    @Test
    public void memAppenderLoggerOutputTest(){

        // Arrange: create a memory appender and a logger, then adds appender to logger
        MemAppender memoryAppender = getDefaultArrayListMemAppenderWithLayout();
        Logger logger = Logger.getLogger(MemAppender.class);
        logger.addAppender(memoryAppender);

        // redirect standard output stream to the output stream to capture the output
        ByteArrayOutputStream mockConsole = new ByteArrayOutputStream();
        System.setOut(new PrintStream(mockConsole));

        logger.info("This is a simple test");

        // Act: print the logs
        memoryAppender.printLogs();

        // Assert: check if the output is correct
        String output = mockConsole.toString();
        Assertions.assertTrue(output.contains("This is a simple test"), "The output does not contain the correct message, the output is\n" + output);
    }

    /**
     * Testing if the max size is correctly set
     */
    @Test
    public void givenMemAppender_whenSettingMaxSize_thenMaxSizeHasCorrectValue(){

        // Arrange: prepare the memory appender using an ArrayList
        MemAppender memoryAppender = MemAppender.getInstance(new ArrayList<>());

        // Act: set the max size to 30
        memoryAppender.setMaxSize(30);

        // Assert: check if the max size is correct
        Assertions.assertEquals(30, memoryAppender.getMaxSize(), "The max size must be 30, but was " + memoryAppender.getMaxSize());
    }

    /**
     * Testing if the memory appender is a singleton by comparing two instances
     */
    @Test
    public void givenTwoInstances_whenComparingThem_thenTheyAreEqual(){

        // Arrange: create two instances of MemAppender
        MemAppender memoryAppender1 = MemAppender.getInstance(new ArrayList<>());
        MemAppender memoryAppender2 = MemAppender.getInstance(new ArrayList<>());

        // Act: compare the two instances
        boolean areEqual = memoryAppender1 == memoryAppender2;

        // Assert: check if the two instances are equal making sure they are singleton
        Assertions.assertTrue(areEqual, "The two instances must be equal");
    }

    /**
     * Testing when max size is not reached, then the discarded events count is zero
     */
    @Test
    public void givenNoDiscardedEvents_whenLogging_thenDiscardedEventsIsZero(){

        // Arrange: create a memory appender instance and set the max size to 100, then add the appender to the logger
        MemAppender memoryAppender = MemAppender.getInstance(new ArrayList<>());
        memoryAppender.setMaxSize(100);

        Logger logger = Logger.getLogger(MemAppender.class);
        logger.addAppender(memoryAppender);

        // Act: log two messages
        logger.info("hello world 1");
        logger.info("hello world 2");

        // Assert: check if the discarded events is zero
        Assertions.assertEquals(0, memoryAppender.getDiscardedLogCount(), "The discarded events must be zero, but was " + memoryAppender.getDiscardedLogCount());
    }

    /**
     * Testing when max size is reached, then the discarded events count is correct
     */
    @Test
    public void givenMultipleDiscardedEvents_whenLogging_thenDiscardedEventsHasCorrectCount(){

        // Arrange: create a memory appender instance and set the max size to 3, then add the appender to the logger
        MemAppender memoryAppender = MemAppender.getInstance(new ArrayList<>());
        memoryAppender.setMaxSize(3);

        Logger logger = Logger.getLogger(MemAppender.class);
        logger.addAppender(memoryAppender);

        // Act: log 5 messages
        int totalLogs = 5;
        for (int i = 0; i < totalLogs; i++){
            logger.info("Hello World " + i);
        }

        // Assert: make sure the discarded events is correct. Knowing that the max size is 3, after 5 logs, then 2 logs should be discarded
        Assertions.assertEquals(2, memoryAppender.getDiscardedLogCount(), "The discarded events must be 2, but was " + memoryAppender.getDiscardedLogCount());
    }

    /**
     * Testing if the event strings correctly return the correct logs
     */
    @Test
    public void givenMultipleLogsEvents_whenCallingGetEventStrings_thenReturnsCorrectLogs(){

        // Arrange: create a memory appender and a logger, then adds appender to logger
        MemAppender memAppender = getDefaultArrayListMemAppenderWithLayout();
        Logger logger = Logger.getLogger(MemAppender.class);
        logger.addAppender(memAppender);

        logger.info("This is info");
        logger.debug("This is debug");

        // Act: get the event strings
        List<String> eventStrings = memAppender.getEventStrings();

        // Assert: check if the event strings are correct
        String log1 = eventStrings.get(0);
        Assertions.assertTrue(log1.startsWith("[INFO]"), "The value does not start with the correct priority" + log1);
        Assertions.assertTrue(log1.contains("This is info"), "The value does not contain the correct message " + log1);

        String log2 = eventStrings.get(1);
        Assertions.assertTrue(log2.startsWith("[DEBUG]"), "The value does not start with the correct priority " + log2);
        Assertions.assertTrue(log2.contains("This is debug"), "The value does not contain the correct message " + log2);

    }

    /**
     * Testing mem appender with pattern layout to result in correct layout of log
     */
    @Test
    public void givenPatternLayout_whenUsingMemAppender_thenLayoutIsCorrect(){

        // Arrange: create a memory appender and a logger, then adds appender to logger.
        MemAppender memAppender = MemAppender.getInstance(new ArrayList<>());
        Logger logger = Logger.getLogger(MemAppender.class);

        memAppender.setLayout(new PatternLayout("%p %m"));      // using pattern layout specifically in the format of "Priority Message"
        logger.addAppender(memAppender);

        // Act: create a log
        logger.info("Hello World");

        // Assert: check if the log is correct using the pattern layout
        List<String> eventStrings = memAppender.getEventStrings();
        String log1 = eventStrings.get(0);

        Assertions.assertEquals("INFO Hello World", log1, "The log must be 'INFO Hello World', but was " + log1);
    }

    /**
     * Testing if the get event strings throws an exception when layout is not set
     */
    @Test
    public void givenNoLayout_whenCallingGetEventStrings_thenThrowsException(){

        // Arrange: create a memory appender and a logger, then adds appender to logger
        MemAppender memAppender = MemAppender.getInstance(new ArrayList<>());
        Logger logger = Logger.getLogger(MemAppender.class);
        logger.addAppender(memAppender);

        // Act: create a log
        logger.info("Hello World");

        // Assert: check if the exception is thrown
        Assertions.assertThrows(IllegalStateException.class, memAppender::getEventStrings, "The exception must be thrown as layout is not set");
    }

    /**
     * Testing if the print logs throws an exception when layout is not set
     */
    @Test
    public void givenNoLayout_whenCallingPrintLogs_thenThrowsException(){

        // Arrange: create a memory appender and a logger, then adds appender to logger
        MemAppender memAppender = MemAppender.getInstance(new ArrayList<>());
        Logger logger = Logger.getLogger(MemAppender.class);
        logger.addAppender(memAppender);

        // Act: create a log
        logger.debug("This is debug");

        // Assert: check if the exception is thrown
        Assertions.assertThrows(IllegalStateException.class, memAppender::printLogs, "The exception must be thrown as layout is not set");
    }
}
