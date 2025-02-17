package org.ayusoftware.log4jenhanced.logging;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Layout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MemAppender stores log events in memory
 */
public class MemAppender extends AppenderSkeleton {

    // The singleton instance
    private static MemAppender instance;

    // The list of log events
    private final List<LoggingEvent> logEvents;

    // The maximum size of the log events
    private int maxSize;

    // The number of discarded log events
    private long discardedLogCount = 0;
    private Layout layout;

    /**
     * Using dependency injection for logEvents by storing the log events in a list
     */
    private MemAppender(List<LoggingEvent> logEvents){
        this.logEvents = logEvents;
        this.maxSize = 100;
    }

    /**
     * Get the singleton instance of MemAppender.
     * @param logEvents the list of log events
     * @return the singleton instance of MemAppender
     */
    public static synchronized MemAppender getInstance(List<LoggingEvent> logEvents){

        if (instance == null){
            instance = new MemAppender(logEvents);
        }
        return instance;
    }

    /**
     * Dispose of the singleton instance
     */
    public static void dispose(){
        instance = null;
    }

    /**
     * Override the append method to handle the incoming log events
     * @param event the log event to be appended
     */
    @Override
    protected void append(LoggingEvent event) {
        if (logEvents.size() >= maxSize){

            // remove the oldest event when the list reaches the maximum size
            logEvents.remove(0);
            discardedLogCount++;
        }

        logEvents.add(event);
    }

    /**
     * Close resources of log events
     */
    @Override
    public void close(){
        logEvents.clear();
        discardedLogCount = 0;
    }

    @Override
    public boolean requiresLayout(){
        return true;
    }

    /**
     * Setter for the maximum size of the log events
     * @param maxSize the maximum size of the log
     */
    public void setMaxSize(int maxSize){
        this.maxSize = maxSize;
    }

    /**
     * Getter for the maximum size of the log events
     * @return the maximum size of the log events
     */
    public int getMaxSize(){
        return maxSize;
    }

    /**
     * Getter for the number of discarded log events
     * @return the number of discarded log events
     */
    public long getDiscardedLogCount(){
        return discardedLogCount;
    }

    /**
     * Setter for the layout
     * @param layout the layout to be set
     */
    @Override
    public void setLayout(Layout layout){
        this.layout = layout;
    }

    /**
     * Getter for the layout
     * @return the layout
     */
    @Override
    public Layout getLayout(){
        return layout;
    }

    /**
     * Get the current logs as unmodifiable list
     * @return unmodifiable list of log events
     */
    public List<LoggingEvent> getCurrentLogs(){
        return Collections.unmodifiableList(logEvents);
    }

    /**
     * Get the current logs as a list of strings
     * @return unmodifiable list of log events as strings
     */
    public List<String> getEventStrings(){

        if (layout == null){
            throw new IllegalStateException("Layout is not set");
        }

        List<String> eventStrings = new ArrayList<>();

        for (LoggingEvent event : logEvents){
            eventStrings.add(layout.format(event));
        }

        return Collections.unmodifiableList(eventStrings);
    }

    /**
     * Print the log events to the console using the layout and clear the log events from memory
     */
    public void printLogs(){

        if (layout == null){
            throw new IllegalStateException("Layout is not set");
        }

        for (LoggingEvent event : logEvents){
            System.out.println(layout.format(event));
        }

        logEvents.clear();
    }
}
