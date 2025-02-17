package org.ayusoftware.log4jenhanced.tests;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;
import org.ayusoftware.log4jenhanced.logging.VelocityLayout;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

/**
 * VelocityLayoutTest for the VelocityLayout class
 */
public class VelocityLayoutTest {

    /**
     * Testing if all the different priority are working correctly with the velocity layout (in the format of "[Priority] Message")
     */
    @ParameterizedTest
    @ValueSource(strings = {"DEBUG", "INFO", "WARN", "ERROR", "FATAL"})
    public void givenLoggingEventsWithDifferentPriority_whenCallingFormat_thenFormatCorrectly(String value){

        // Arrange: create a VelocityLayout and get the level from the value string
        VelocityLayout velocityLayout = new VelocityLayout("[$p] $m");
        Level level = Level.toLevel(value);

        // Act: create log events and call the format method
        LoggingEvent loggingEvent = new LoggingEvent("org.ayusoftware.log4jenhanced.tests", Logger.getLogger(VelocityLayoutTest.class), System.currentTimeMillis(), level, "Hello World!", null);
        String result = velocityLayout.format(loggingEvent);

        // Assert: make sure the level and message are formatted correctly
        Assertions.assertEquals("[" + level.toString() + "] Hello World!", result, "Level and message are not formatted correctly, level: " + level);

    }

    /**
     * Testing if format method of VelocityLayout class formats the logging event correctly (in this case, checking the category)
     */
    @Test
    public void givenLoggingEvent_whenCallingFormatWithCategory_thenFormatCorrectly(){

        // Arrange: create a VelocityLayout and a log event
        VelocityLayout velocityLayout = new VelocityLayout("$c");
        LoggingEvent loggingEvent = new LoggingEvent("org.ayusoftware.log4jenhanced.tests", Logger.getLogger(VelocityLayoutTest.class), Level.INFO, "Test message", null);

        // Act: call the format method
        String result = velocityLayout.format(loggingEvent);

        // Assert: make sure the category is formatted correctly
        Assertions.assertEquals("org.ayusoftware.log4jenhanced.tests.VelocityLayoutTest", result, "Category is not formatted correctly");

    }

    /**
     * Testing if format method of VelocityLayout class formats the logging event correctly (in this case, checking the date)
     */
    @Test
    public void givenLoggingEvent_whenCallingFormatWithDate_thenFormatCorrectly(){

        // Arrange: get current time in milliseconds and format it into a string, then create a VelocityLayout and a log event
        long timeStamp = System.currentTimeMillis();
        String formattedTimeStamp = new Date(timeStamp).toString();

        VelocityLayout velocityLayout = new VelocityLayout("$d");
        LoggingEvent loggingEvent = new LoggingEvent("org.ayusoftware.log4jenhanced.tests", Logger.getLogger(VelocityLayoutTest.class), timeStamp, Level.INFO, "Test message", null);

        // Act: call the format method (which the date will be formatted as a string as well)
        String result = velocityLayout.format(loggingEvent);

        // Assert: make sure the date is formatted correctly
        Assertions.assertEquals(formattedTimeStamp, result, "Date is not formatted correctly");

    }

    /**
     * Testing if the format method of VelocityLayout class formats the logging event correctly (in this case, checking the message)
     */
    @Test
    public void givenLoggingEvent_whenCallingFormatWithMessage_thenFormatCorrectly(){

        // Arrange: create a VelocityLayout and a log event
        VelocityLayout velocityLayout = new VelocityLayout("$m");
        LoggingEvent loggingEvent = new LoggingEvent("org.ayusoftware.log4jenhanced.tests", Logger.getLogger(VelocityLayoutTest.class), Level.INFO, "Test message", null);

        // Act: call the format method
        String result = velocityLayout.format(loggingEvent);

        // Assert: make sure the message is correct
        Assertions.assertEquals("Test message", result, "Message is not formatted correctly");
    }

    /**
     * Testing if the format method of VelocityLayout class formats the logging event correctly (in this case, checking the priority)
     */
    @Test
    public void givenLoggingEvent_whenCallingFormatWithPriority_thenFormatCorrectly(){

        // Arrange: create a VelocityLayout and a log event
        VelocityLayout velocityLayout = new VelocityLayout("$p");
        LoggingEvent loggingEvent = new LoggingEvent("org.ayusoftware.log4jenhanced.tests", Logger.getLogger(VelocityLayoutTest.class), Level.INFO, "Test message", null);

        // Act: call the format method
        String result = velocityLayout.format(loggingEvent);

        // Assert: make sure the priority is correct
        Assertions.assertEquals("INFO", result, "Priority is not formatted correctly");
    }

    /**
     * Testing if the format method of VelocityLayout class formats the logging event correctly (in this case, checking the thread)
     */
    @Test
    public void givenLoggingEvent_whenCallingFormatWithThread_thenFormatCorrectly(){

        // Arrange: create a VelocityLayout and a log event
        VelocityLayout velocityLayout = new VelocityLayout("$t");
        LoggingEvent loggingEvent = new LoggingEvent("org.ayusoftware.log4jenhanced.tests", Logger.getLogger(VelocityLayoutTest.class), Level.INFO, "Test message", null);

        // Act: call the format method
        String result = velocityLayout.format(loggingEvent);

        // Assert: make sure the thread is correct
        Assertions.assertEquals(loggingEvent.getThreadName(), result);
    }

    /**
     * Testing if the format method of VelocityLayout class formats the logging event correctly (in this case, checking the new line)
     */
    @Test
    public void givenLoggingEvent_whenCallingFormatWithNewLine_thenFormatCorrectly(){

        // Arrange: create a VelocityLayout and a log event
        VelocityLayout velocityLayout = new VelocityLayout("$n");
        LoggingEvent loggingEvent = new LoggingEvent("org.ayusoftware.log4jenhanced.tests", Logger.getLogger(VelocityLayoutTest.class), Level.INFO, "Test message", null);

        // Act: call the format method
        String result = velocityLayout.format(loggingEvent);

        // Assert: make sure the new line is correct (using this particular System.lineSeparator() method, so that it will work on unix and Windows systems as well)
        Assertions.assertEquals(System.lineSeparator(), result);
    }

    /**
     * Testing if the velocity layout with the console appender is applied correctly (using format of "Priority Message")
     */
    @Test
    public void givenConsoleAppender_whenApplyingVelocityLayout_thenLayoutAppliedCorrectly(){

        // Arrange: create a ByteArrayOutputStream and redirects the standard output stream to it.
        // Then set up the console appender with the velocity layout and adds it to the logger
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setLayout(new VelocityLayout("[$p] $m"));
        consoleAppender.setTarget("System.out");
        consoleAppender.activateOptions();

        Logger logger = Logger.getLogger(VelocityLayoutTest.class);
        logger.addAppender(consoleAppender);

        // Act: log a message
        logger.info("hello world");

        // Assert: make sure the output matches the expected format
        String output = outContent.toString();
        Assertions.assertEquals("[INFO] hello world", output, "Layout is not applied correctly");

    }

    /**
     * Testing if the velocity layout is applied correctly to the file appender
     */
    @Test
    public void givenFileAppender_whenApplyingVelocityLayout_thenLayoutIsAppliedCorrectly() throws IOException  {

        // Arrange: prepare the file path and the file appender with the velocity layout, then add the appender to the logger
        Path filePath = Path.of("fileAppenderTest.log");
        FileAppender fileAppender = new FileAppender(new VelocityLayout("[$p] $m"), "fileAppenderTest.log");

        try {
            Logger logger = Logger.getLogger(VelocityLayoutTest.class);
            logger.addAppender(fileAppender);

            // Act: log a message
            logger.debug("This is debug message!");

            // Assert: make sure the file content matches the expected format
            String result = Files.readString(filePath);

            Assertions.assertEquals("[DEBUG] This is debug message!", result, "Layout is not applied correctly");

        } finally {

            // clean up no matter if the test fail/pass (close the file appender and delete the file)
            fileAppender.close();
            Files.deleteIfExists(filePath);
        }
    }
}
