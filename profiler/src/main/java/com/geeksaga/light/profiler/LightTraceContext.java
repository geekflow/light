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
package com.geeksaga.light.profiler;

import com.geeksaga.light.agent.TraceContext;
import com.geeksaga.light.agent.trace.Trace;

/**
 * @author geeksaga
 */
public class LightTraceContext implements TraceContext {

    private final ThreadLocal<Trace> threadLocal = new ThreadLocal<Trace>();

    public LightTraceContext() {}

    public Trace newTrace() {
        Trace  trace = new Trace() {
            @Override
            public void begin() {
            }

            @Override
            public void end() {
            }
        };

        set(trace);

        return trace;
    }

    public Trace currentTrace() {
        return threadLocal.get();
    }

    public void removeTrace() {
        threadLocal.remove();
    }

    private void set(Trace trace) {
        threadLocal.set(trace);
    }
}
