package com.tkforgeworks.cookconnect.recipeservice.message.consumer;

import com.tkforgeworks.cookconnect.recipeservice.message.model.UserChangeEvent;
import com.tkforgeworks.cookconnect.recipeservice.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class UserEventConsumer {
    private final RecipeService recipeService;

    @Bean
    public Consumer<UserChangeEvent> handleUserChange(){
        return event -> {
            log.debug("Received UserChange event: {}", event);

            try {
                switch (event.getChangeType()){
                    case ACCOUNT_DELETED -> {
                        recipeService.handleUserAccountStatus(event, "delete"); //TODO: Change 'status' to some enum to avoid string use
                        log.debug("Account deleted");
                    }
                    case ACCOUNT_CLOSED -> {
                        recipeService.handleUserAccountStatus(event, "closed");
                        log.debug("Account closed");
                    }
                    case PRIVACY_STATUS ->  {
                        recipeService.handleUserAccountStatus(event, "private");
                        log.debug("Privacy status");
                    }
                    default -> {
                        log.debug("Event of type {} not handled", event.getChangeType());
                    }
                }
            } catch (Exception ex) {
                log.error("Error handling UserChange event", ex);
                throw ex; //TODO: More verbose exception output here
            }
        };
    }
}