/*
 *  Copyright (c) 2025 Original Author(s), PhonePe India Pvt. Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
