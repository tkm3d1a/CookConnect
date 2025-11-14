package com.tkforgeworks.cookconnect.recipeservice.common;

import org.springframework.util.Assert;

public class UserContextHolder {
    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    public static UserContext getUserContext() {
        UserContext userContext = CONTEXT.get();
        if (userContext == null) {
            userContext = createEmptyContext();
            CONTEXT.set(userContext);
        }
        return userContext;
    }

    public static void setContext(UserContext userContext) {
        Assert.notNull(userContext, "UserContext must not be null");
        CONTEXT.set(userContext);
    }

    public static UserContext createEmptyContext() {
        return new UserContext();
    }
}
