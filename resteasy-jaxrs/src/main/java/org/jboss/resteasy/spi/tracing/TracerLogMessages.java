package org.jboss.resteasy.spi.tracing;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
@MessageLogger(projectCode = "RESTEASY")
public interface TracerLogMessages extends BasicLogger
{
   TracerLogMessages LOGGER = Logger.getMessageLogger(TracerLogMessages.class, TracerLogMessages.class.getPackage().getName());
   int TRACINGBASE = 7000;
   @Message(id = TRACINGBASE + 0, value = "Operation %s : Duration: %s")
   String logSpan(String operation, long duration);
}
