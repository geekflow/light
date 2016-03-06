/*
 * Copyright 2015 GeekSaga.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.geeksaga.light.agent.core;

import com.geeksaga.light.agent.trace.Trace;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author geeksaga
 */
public class TraceRegistry {
    private static TraceRegistryAdaptor registry = TraceRegistryAdaptor.NULL;

    private static final Lock LOCK = new ReentrantLock();

    public static TraceRegistryAdaptor getTraceRegistryAdaptor() {
        return registry;
    }

    public static void bind(TraceRegistryAdaptor registry) {
        LOCK.lock();

        try {
            TraceRegistry.registry = registry;
        }
        finally {
            LOCK.unlock();
        }
    }

    public static void unbind() {
        LOCK.lock();

        try {
            registry = TraceRegistryAdaptor.NULL;
        } finally {
            LOCK.unlock();
        }
    }

    public static Trace get(int id) {
        return registry.get(id);
    }
}
