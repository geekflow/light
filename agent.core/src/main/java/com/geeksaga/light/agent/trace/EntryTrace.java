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

import com.geeksaga.light.agent.TraceContext;
import com.geeksaga.light.agent.core.ActiveObject;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author geeksaga
 */
public class EntryTrace implements Trace {
    private static final Logger logger = Logger.getLogger(EntryTrace.class.getName());

    private TraceContext traceContext;

    public EntryTrace(TraceContext traceContext) {
        this.traceContext = traceContext;
    }

    public void begin(MethodInfo methodInfo) {
        try {
            logger.info(methodInfo.getName() + methodInfo.getDesc());

            ActiveObject activeObject = create(methodInfo);
            activeObject.setStartTime(System.currentTimeMillis());

            logger.info(activeObject.toString());
        } catch (Throwable throwable) {
            logger.log(Level.INFO, throwable.getMessage(), throwable);
        }
    }

    public void end(MethodInfo methodInfo, Throwable throwable) {
        try {
            logger.info(String.valueOf(methodInfo.getParameter().size()) + "=" + Arrays.toString(methodInfo.getParameter().getValues()));
            ActiveObject activeObject = traceContext.current();

            logger.info("start time = " + activeObject.getStartTime());
            logger.info("end time = " + System.currentTimeMillis());
            logger.info("elapsed time = " + String.valueOf(System.currentTimeMillis() - activeObject.getStartTime()));

            logger.info(activeObject.toString());
        } finally {
            traceContext.remove();
        }
    }

    private ActiveObject create(MethodInfo methodInfo) {
        return traceContext.create(methodInfo);
    }
}
