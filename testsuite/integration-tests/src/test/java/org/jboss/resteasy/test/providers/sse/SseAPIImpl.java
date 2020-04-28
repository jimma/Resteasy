package org.jboss.resteasy.test.providers.sse;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

@Path("/apitest")
public class SseAPIImpl implements SseAPI {

    private static SseEventSink sseSink;
    private static Sse sse;

    @Override
    public void events(SseEventSink sseSink, Sse sse) {
        SseAPIImpl.sseSink = sseSink;
        SseAPIImpl.sse = sse;
    }

    @Override
    public void send() {
        if (sseSink == null) {
            throw new IllegalStateException("No SseSink is attached.");
        }
        OutboundSseEvent.Builder eventBuilder = sse.newEventBuilder();
        OutboundSseEvent sseEvent = eventBuilder.name("SseEvent")
                .mediaType(MediaType.TEXT_PLAIN_TYPE).data(String.class, "AnnotationInheritedEvent")
                .comment("Sse Event").build();
        sseSink.send(sseEvent);
    }

}