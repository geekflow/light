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

import com.geeksaga.light.agent.TraceContext;
import com.geeksaga.light.agent.config.Config;
import com.geeksaga.light.agent.trace.EntryTrace;
import com.geeksaga.light.agent.trace.MethodInfo;
import com.geeksaga.light.agent.trace.Trace;

/**
 * @author geeksaga
 */
public class AgentTraceContext implements TraceContext {

    private final ThreadLocal<ActiveObject> threadLocal = new ThreadLocal<ActiveObject>();
    private Config config;

    public AgentTraceContext(Config config) {
        this.config = config;
    }

    public ActiveObject create(MethodInfo methodInfo) {
        return set(new ActiveObject(Thread.currentThread(), methodInfo));
    }

    public ActiveObject current() {
        return threadLocal.get();
    }

    public void remove() {
        threadLocal.remove();
    }

    private ActiveObject set(ActiveObject activeObject) {
        threadLocal.set(activeObject);

        return activeObject;
    }

    public Config getConfig() {
        return config;
    }
}
