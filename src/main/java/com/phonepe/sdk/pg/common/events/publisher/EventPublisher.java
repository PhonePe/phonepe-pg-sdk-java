package com.phonepe.sdk.pg.common.events.publisher;

import com.phonepe.sdk.pg.common.events.models.BaseEvent;
import java.util.function.Supplier;

public interface EventPublisher extends Runnable {

    default void setAuthTokenSupplier(Supplier<String> authTokenSuppplier) {

    }

    default void startPublishingEvents(Supplier<String> authTokenSupplier) {

    }

    default void send(BaseEvent baseEvent) {

    }

}
