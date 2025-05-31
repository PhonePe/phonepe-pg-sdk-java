package com.phonepe.sdk.pg.common.events.queue;

import com.phonepe.sdk.pg.common.events.models.BaseEvent;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface EventQueue {

    default void add(BaseEvent data) {

    }

    default boolean isEmpty() {
        return true;
    }

    default int size() {
        return 0;
    }

    default BaseEvent poll() {
        return null;
    }

    default ConcurrentLinkedQueue<BaseEvent> getQueue() {
        return null;
    }
}
