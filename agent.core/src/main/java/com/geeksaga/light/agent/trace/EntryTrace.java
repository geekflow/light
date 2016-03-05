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

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author geeksaga
 */
public class EntryTrace {
    private static final Logger logger = Logger.getLogger(DebugTrace.class.getName());

    private static Trace trace = new Trace() {
        @Override
        public void begin(MethodInfo methodInfo) {
        }

        @Override
        public void end(MethodInfo methodInfo, Throwable throwable) {
            logger.info(String.valueOf(methodInfo.getParameter().size()) + "=" + Arrays.toString(methodInfo.getParameter().getValues()));
        }
    };

    public static void set(Trace trace) {
        if (trace != null) {
            EntryTrace.trace = trace;
        }
    }

    public static void begin(MethodInfo methodInfo) {

    }

    public static void end(MethodInfo methodInfo, Throwable throwable) {
        trace.end(methodInfo, throwable);
    }
}
