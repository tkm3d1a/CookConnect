package com.tkforgeworks.cookconnect.userservice.message.producer;

import com.tkforgeworks.cookconnect.userservice.message.model.UserChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserEventPublisher {
    private final StreamBridge streamBridge;

    @Value("${tkforgeworks.kafka.user-change}")
    private String userChangeBinding;

    public void publishUserChange(UserChangeEvent userChangeEvent) {
        log.debug("Publishing UserChangeEvent to {}: UserId - {}\tChange Type - {}",
                userChangeBinding,
                userChangeEvent.getUserId(),
                userChangeEvent.getChangeType());
        streamBridge.send(userChangeBinding, userChangeEvent);
        log.debug("Published UserChangeEvent to {} successfully", userChangeBinding);
    }
}
