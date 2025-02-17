package org.ayusoftware.log4jenhanced.tests;

import org.apache.log4j.*;
import org.ayusoftware.log4jenhanced.logging.MemAppender;
import org.ayusoftware.log4jenhanced.logging.VelocityLayout;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * StressTest for testing performance of different appenders and layouts with a large number of logs.
 * I commented out the @Tag("stress") annotation so that "mvn test" will proceed to run all tests including StressTest class.
 * But if it caused any issues, please uncomment the annotation and also the <excludedGroups>stress</excludedGroups> inside Surefire plugin in pom.xml.
 * Once uncommented both, the stress tests will be excluded from the mvn test run and should not jam the system (as stress tests can be resource-intensive).
 */
//@Tag("stress")
public class StressTest {
    static final int totalLogs = 3_000_000;
    private final Logger resultLogger;

    /**
     * Constructor for StressTest, creates a file appender and logger to log results of each stress test
     * @throws IOException if an I/O error occurs
     */
    public StressTest() throws IOException {

        // create file appender and logger to log results of each stress test
        FileAppender resultAppender = new FileAppender();
        resultAppender.setLayout(new PatternLayout("%m%n"));
        resultAppender.setFile("logs/stress_test_result.log");
        resultAppender.activateOptions();

        resultLogger = Logger.getLogger("resultLogger");
        resultLogger.removeAllAppenders();
        resultLogger.addAppender(resultAppender);

    }

    /**
     * Getter for VelocityLayout
     * @return a VelocityLayout object
     */
    private static Layout getVelocityLayout(){
        return new VelocityLayout("$p $c $d $m $t $n");
    }

    /**
     * Getter for PatternLayout
     * @return a PatternLayout object
     */
    private static Layout getPatternLayout(){
        return new PatternLayout("%p %c %d{yyyy-MM-dd HH:mm:ss} %m %t %n");
    }

    /**
     * Stress testing using MemAppender with Velocity Layout with an ArrayList, and making sure that logs are not discarded.
     */
    @Test
    public void stressTest_memAppender_velocity_arrayList_noDiscard(){

        // Arrange: get the method name of the current test. Create a MemAppender with an ArrayList and set the layout and max size.
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        MemAppender memAppender = MemAppender.getInstance(new ArrayList<>());
        memAppender.setLayout(getVelocityLayout());
        memAppender.setMaxSize(totalLogs);

        // Act: perform the test (passing the method name, memAppender, and total logs)
        performTest(methodName, memAppender, totalLogs);

        // Assert: make sure that logs are not discarded (discarded log count is 0)
        Assertions.assertEquals(0, memAppender.getDiscardedLogCount(), "Discarded log count must be 0 since there should not be any discarded logs.");

    }

    /**
     * Stress testing using MemAppender with Velocity Layout with a LinkedList, and making sure that logs are not discarded.
     */
    @Test
    public void stressTest_memAppender_velocity_linkedList_noDiscard(){

        // Arrange: get the method name of the current test. Create a MemAppender with a LinkedList and set the layout and max size.
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        MemAppender memAppender = MemAppender.getInstance(new LinkedList<>());
        memAppender.setLayout(getVelocityLayout());
        memAppender.setMaxSize(totalLogs);

        // Act: perform the test (passing the method name, memAppender, and total logs)
        performTest(methodName, memAppender, totalLogs);

        // Assert: make sure that logs are not discarded (discarded log count is 0)
        Assertions.assertEquals(0, memAppender.getDiscardedLogCount(), "Discarded log count must be 0 since there should not be any discarded logs.");
    }

    /**
     * Stress testing the ConsoleAppender with Velocity Layout.
     */
    @Test
    public void stressTest_consoleAppender_velocity(){

        // Arrange: get the method name of the current test. Create a ConsoleAppender and set the layout and target.
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setLayout(getVelocityLayout());
        consoleAppender.setTarget("System.out");
        consoleAppender.activateOptions();

        // Act: perform the test (passing the method name, consoleAppender, and total logs)
        performTest(methodName, consoleAppender, totalLogs);
    }

    /**
     * Stress testing the FileAppender with Velocity Layout.
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void stressTest_fileAppender_velocity() throws IOException {

        // Arrange: get the method name of the current test. Create a temporary file and a FileAppender with the velocity layout.
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        Path tempFile = Files.createTempFile("stress_test_file_appender", ".log");

        FileAppender fileAppender = new FileAppender();
        fileAppender.setLayout(getVelocityLayout());
        fileAppender.setFile(tempFile.getFileName().toString());
        fileAppender.activateOptions();

        try {
            // Act: perform the test (passing the method name, fileAppender, and total logs)
            performTest(methodName, fileAppender, totalLogs);

        } finally {
            // clean up no matter if the test fail/pass. Always will close the file appender and delete the file.
            fileAppender.close();
            Files.deleteIfExists(tempFile.getFileName());
        }
    }

    /**
     * Stress testing the MemAppender with Pattern Layout (Comparing between Pattern Layout and Velocity Layout).
     */
    @Test
    public void stressTest_patternLayout(){

        // Arrange: determine the total logs, then get the method name of the current test and create a MemAppender with an ArrayList.
        int extendedTotalLogs = 5_000_000;
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        MemAppender memAppender = MemAppender.getInstance(new ArrayList<>());
        memAppender.setLayout(getPatternLayout());
        memAppender.setMaxSize(extendedTotalLogs);

        // Act: perform the test (passing the method name, memAppender, and extended total logs)
        performTest(methodName, memAppender, extendedTotalLogs);

    }

    /**
     * Stress testing the MemAppender with Velocity Layout (Comparing between Pattern Layout and Velocity Layout).
     */
    @Test
    public void stressTest_velocityLayout(){

        // Arrange: determine the total logs, then get the method name of the current test and create a MemAppender with an ArrayList.
        int extendedTotalLogs = 5_000_000;
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        MemAppender memAppender = MemAppender.getInstance(new ArrayList<>());
        memAppender.setLayout(getVelocityLayout());
        memAppender.setMaxSize(extendedTotalLogs);

        // Act: perform the test (passing the method name, memAppender, and extended total logs)
        performTest(methodName, memAppender, extendedTotalLogs);

    }

    /**
     * Stress testing the MemAppender with Velocity Layout and using an ArrayList, checking if the discarded logs are correct.
     * @param keepingPercentage the percentage of logs to keep
     */
    @ParameterizedTest
    @ValueSource(floats = {0.1f, 0.5f, 0.9f})
    public void stressTest_memAppender_velocity_arrayList_usingDiscard(float keepingPercentage){

        // Arrange: get the method name of the current test. Create a MemAppender with an ArrayList and set the layout and max size.
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        MemAppender memAppender = MemAppender.getInstance(new ArrayList<>());
        memAppender.setLayout(getVelocityLayout());
        memAppender.setMaxSize((int) (totalLogs * keepingPercentage));
        memAppender.close();    // clear the results from the previous run

        // Act: perform the test (passing the method name, memAppender, and max size that is passed as parameter)
        performTest(methodName + "_keeping - " + keepingPercentage * 100 + "_percent", memAppender, totalLogs);

        // Assert: make sure that the number of discarded log counts is correct
        Assertions.assertEquals((int) (totalLogs * (1 - keepingPercentage)), memAppender.getDiscardedLogCount(), "Discarded log count must be " + (1 - keepingPercentage) * 100 + "% of the total logs");

    }

    /**
     * Stress testing the MemAppender with Velocity Layout and using a LinkedList, checking if the discarded logs are correct.
     * @param keepingPercentage the percentage of logs to keep
     */
    @ParameterizedTest
    @ValueSource(floats = {0.1f, 0.5f, 0.9f})
    public void stressTest_memAppender_velocity_linkedList_usingDiscard(float keepingPercentage){

        // Arrange: get the method name of the current test. Create a MemAppender with an ArrayList and set the layout and max size.
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        MemAppender memAppender = MemAppender.getInstance(new LinkedList<>());
        memAppender.setLayout(getVelocityLayout());
        memAppender.setMaxSize((int) (totalLogs * keepingPercentage));
        memAppender.close();    // clear the results from the previous run

        // Act: perform the test (passing the method name, memAppender, and max size that is passed as parameter)
        performTest(methodName + "_keeping - " + keepingPercentage * 100 + "_percent", memAppender, totalLogs);

        // Assert: make sure that the number of discarded log counts is correct
        Assertions.assertEquals((int) (totalLogs * (1 - keepingPercentage)), memAppender.getDiscardedLogCount(), "Discarded log count must be " + (1 - keepingPercentage) * 100 + "% of the total logs");

    }

    /**
     * Perform the stress test by getting the logger, adding the appender, calculating the duration, and logging the result.
     * @param testName the name of the test
     * @param appender the appender to be added to the logger
     * @param logCount the number of logs to be logged
     */
    private void performTest(String testName, Appender appender, long logCount){
        Logger logger = Logger.getLogger(testName);
        logger.removeAllAppenders();
        logger.addAppender(appender);

        long startTime = System.nanoTime();

        for (int i = 0; i < logCount; i++){
            logger.info(String.format("Hello World - %d", i));
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        float durationSeconds = (float) duration / 1_000_000_000;

        long logsPerSecond = (long) (logCount / durationSeconds);

        // output the result to the result logger in the format of "test name - duration - (logs per second)"
        resultLogger.info(String.format("%s - %s (%,d logs per second)", testName, formatDuration(duration), logsPerSecond));
    }

    /**
     * Format the duration in minutes, seconds, milliseconds, microseconds for the result logger
     * @param time the time to be formatted
     * @return the formatted time as string
     */
    private static String formatDuration(long time){
        long minutes = TimeUnit.NANOSECONDS.toMinutes(time);
        time -= TimeUnit.MINUTES.toNanos(minutes);

        long seconds = TimeUnit.NANOSECONDS.toSeconds(time);
        time -= TimeUnit.SECONDS.toNanos(seconds);

        long milliseconds = TimeUnit.NANOSECONDS.toMillis(time);
        time -= TimeUnit.MILLISECONDS.toNanos(milliseconds);

        long microseconds = TimeUnit.NANOSECONDS.toMicros(time);
        time -= TimeUnit.MICROSECONDS.toNanos(microseconds);

        return String.format("%02d m, %02d s, %03d ms, %03d us, %03d ns", minutes, seconds, milliseconds, microseconds, time);
    }
}
