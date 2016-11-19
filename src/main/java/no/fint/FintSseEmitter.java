package no.fint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FintSseEmitter {
    private String orgId;
    private SseEmitter sseEmitter;
}
