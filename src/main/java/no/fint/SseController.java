package no.fint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class SseController {
    private final List<FintSseEmitter> emitters = new ArrayList<>();
    private int counter = 0;

    @RequestMapping(value = "/subscribe", method = RequestMethod.GET)
    public SseEmitter subscribe(@RequestHeader("x-org-id") String orgId) {
        SseEmitter emitter = new SseEmitter(10000000L);
        FintSseEmitter fintSseEmitter = new FintSseEmitter(orgId, emitter);
        emitter.onCompletion(() -> emitters.remove(fintSseEmitter));
        emitters.add(fintSseEmitter);
        return emitter;
    }

    @Scheduled(initialDelay = 10000, fixedDelay = 5000)
    public void sendMessage() {
        counter++;
        List<FintSseEmitter> toBeRemoved = new ArrayList<>();
        emitters.forEach((emitter) -> {
            try {
                Message message = new Message("sse-demo", emitter.getOrgId(), ("counter " + counter));
                SseEmitter.SseEventBuilder builder = SseEmitter.event().id(String.valueOf(counter)).name("test-event").data(message);
                emitter.getSseEmitter().send(builder);
                log.info("Message sent: {} ({})", message.getMessage(), message.getOrgId());
            } catch (IOException | IllegalStateException e) {
                toBeRemoved.add(emitter);
                log.warn("Exception when trying to send message, removing subscriber");
            }
        });

        toBeRemoved.forEach(emitters::remove);
    }

}
