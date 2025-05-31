package com.phonepe.sdk.pg.common.events.queue;

import com.phonepe.sdk.pg.common.events.models.BaseEvent;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class BoundedConcurrentLinkedQueue implements EventQueue {

    private ConcurrentLinkedQueue<BaseEvent> queue;
    private int maxSize;

    public BoundedConcurrentLinkedQueue(int maxSize) {
        this.queue = new ConcurrentLinkedQueue<>();
        this.maxSize = maxSize;
    }

    @Override
    public void add(BaseEvent data) {
        if (queue.size() < maxSize && Objects.nonNull(data)) {
            queue.add(data);
        } else {
            log.error("Reached queue max size, skipping event {}", data.getEventName());
        }
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public BaseEvent poll() {
        return queue.poll();
    }

    @Override
    public ConcurrentLinkedQueue<BaseEvent> getQueue() {
        return queue;
    }
}
