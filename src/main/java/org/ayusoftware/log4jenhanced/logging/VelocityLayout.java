package org.ayusoftware.log4jenhanced.logging;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import java.io.StringWriter;
import java.util.Date;

/**
 * VelocityLayout uses velocity as template engine to format the log events
 */
public class VelocityLayout extends Layout {
    private final String pattern;
    private VelocityEngine velocityEngine;

    /**
     * Constructor for VelocityLayout
     * @param pattern the pattern to be used
     */
    public VelocityLayout(String pattern){
        this.pattern = pattern;
        initialiseVelocityEngine();
    }

    /**
     * Format the log event using velocity templates
     * It creates a velocity context and populate it with log events properties
     * @param event the log event to be formatted
     * @return the formatted log event as string
     */
    public String format(LoggingEvent event){

        VelocityContext context = new VelocityContext();

        context.put("c", event.getLoggerName());                            // category
        context.put("d", new Date(event.getTimeStamp()).toString());        // date
        context.put("m", event.getRenderedMessage());                       // message
        context.put("p", event.getLevel().toString());                      // priority
        context.put("t", event.getThreadName());                            // thread
        context.put("n", System.lineSeparator());                           // newline separator

        StringWriter stringWriter = new StringWriter();
        velocityEngine.evaluate(context, stringWriter, "VelocityLayout", pattern);

        return stringWriter.toString();
    }

    @Override
    public boolean ignoresThrowable() {
        return false;
    }

    @Override
    public void activateOptions() {
        // do nothing since there are no options to activate
    }

    /**
     * Initialise the VelocityEngine
     * Sets runtime log system class property to NullLogChute avoiding unnecessary logging by velocity
     */
    private void initialiseVelocityEngine(){
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogChute");
        velocityEngine.init();
    }

}
