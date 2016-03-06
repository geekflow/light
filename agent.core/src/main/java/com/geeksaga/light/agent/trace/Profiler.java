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
package com.geeksaga.light.agent.trace;

import com.geeksaga.light.agent.core.TraceRegistry;

import static org.objectweb.asm.Type.getInternalName;

/**
 * @author geeksaga
 */
public class Profiler {
    public static final String INTERNAL_CLASS_NAME = getInternalName(Profiler.class);
    public static final String BEGIN = "begin";
    public static final String BEGIN_DESCRIPTOR = "(IL" + getInternalName(MethodInfo.class) + ";)V";
    public static final String END = "end";
    public static final String END_DESCRIPTOR = "(IL" + getInternalName(MethodInfo.class) + ";L" + getInternalName(Throwable.class) + ";)V";

    public static void begin(int traceKey, MethodInfo methodInfo) {
        Trace trace = TraceRegistry.get(traceKey);
        trace.begin(methodInfo);
    }

    public static void end(int traceKey, MethodInfo methodInfo, Throwable throwable) {
        Trace trace = TraceRegistry.get(traceKey);
        trace.end(methodInfo, throwable);
    }
}
