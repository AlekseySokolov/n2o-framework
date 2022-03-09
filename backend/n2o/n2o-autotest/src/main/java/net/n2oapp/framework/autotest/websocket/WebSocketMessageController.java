package net.n2oapp.framework.autotest.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketMessageController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Value("${n2o.stomp.session-id}")
    private String sessionId;

    public void sendCount(String destination, Integer count) {
        BadgeMessage message = new BadgeMessage();
        message.setCount(count);
        messagingTemplate.convertAndSendToUser(sessionId, destination, message);
    }

    public void sendColor(String destination, BadgeColor color) {
        BadgeMessage message = new BadgeMessage();
        message.setColor(color.toString());
        messagingTemplate.convertAndSendToUser(sessionId, destination, message);
    }
}
