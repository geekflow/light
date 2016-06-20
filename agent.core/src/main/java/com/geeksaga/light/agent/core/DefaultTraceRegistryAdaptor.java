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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * @author geeksaga
 */
public class DefaultTraceRegistryAdaptor implements TraceRegistryAdaptor
{
    public static final int DEFAULT_MAX_LENGTH = 8;
    private final AtomicInteger id = new AtomicInteger(0);
    private final AtomicReferenceArray<Trace> atomicArray;

    public DefaultTraceRegistryAdaptor()
    {
        this(DEFAULT_MAX_LENGTH);
    }

    public DefaultTraceRegistryAdaptor(int length)
    {
        atomicArray = new AtomicReferenceArray<Trace>(length);
    }

    @Override
    public int add(Trace trace)
    {
        if (trace == null)
        {
            return -1;
        }

        final int newId = id.getAndIncrement();

        atomicArray.set(newId, trace);

        return newId;
    }

    @Override
    public Trace get(int id)
    {
        return atomicArray.get(id);
    }
}
