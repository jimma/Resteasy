package org.jboss.resteasy.test.providers.sse;

import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.logging.Logger;

@Path("/apitest")
public class SseAPIImpl implements SseAPI {
    private static final Logger logger = Logger.getLogger(SseAPIImpl.class);
    private SseEventSink sseSink;
    @Context
    private Sse sse;

    @Override
    public void events(SseEventSink evnetSink) {
        sseSink = evnetSink;
    }

    @Override
    public void send(String message) {
        if (sseSink == null) {
            throw new IllegalStateException("No SseSink is attached.");
        }
        if (sse == null) {
            throw new  IllegalStateException("No Sse injected");
        }
        logger.info("********************SSEAPITest********************** : sending message : " + message);
        sseSink.send(sse.newEvent(message));
        logger.info("********************SSEAPITest********************** : message sent");
    }

}