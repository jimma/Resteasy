package org.jboss.resteasy.test.providers.sse;

import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
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
        OutboundSseEvent.Builder eventBuilder = sse.newEventBuilder();
        OutboundSseEvent sseEvent = eventBuilder.name("SseEvent")
                .mediaType(MediaType.TEXT_PLAIN_TYPE).data(String.class, message)
                .comment("Sse Event").build();
        sseSink.send(sseEvent);
    }

}