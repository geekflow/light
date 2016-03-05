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

import com.geeksaga.light.agent.trace.MethodInfo;
import com.geeksaga.light.agent.trace.Trace;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author geeksaga
 */
public interface TraceRegistryAdaptor {
    int add(Trace trace);
    Trace get(int id);

    TraceRegistryAdaptor NULL = new  TraceRegistryAdaptor() {
        @Override
        public int add(Trace trace) {
            return 0;
        }

        @Override
        public Trace get(int id) {
            return null;
        }
    };
}
