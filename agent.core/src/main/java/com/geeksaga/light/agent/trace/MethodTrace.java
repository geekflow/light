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
import com.geeksaga.light.agent.TraceRepository;
import com.geeksaga.light.agent.core.ActiveObject;
import com.geeksaga.light.agent.profile.ProfileMethod;
import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;

/**
 * @author geeksaga
 */
public class MethodTrace implements Trace
{
    private LightLogger logger;
    private TraceContext traceContext;
    private TraceRepository traceRepository;

    public MethodTrace(TraceContext traceContext)
    {
        this.logger = CommonLogger.getLogger(getClass().getName());
        this.traceContext = traceContext;
    }

    public MethodTrace(TraceContext traceContext, TraceRepository traceRepository)
    {
        this.logger = CommonLogger.getLogger(getClass().getName());
        this.traceContext = traceContext;
        this.traceRepository = traceRepository;
    }

    public void begin(MethodInfo methodInfo)
    {
        logger.info(methodInfo.getName() + methodInfo.getDesc());

        ActiveObject activeObject = traceContext.current();
        if (activeObject == null)
        {
            return;
        }

        ProfileMethod profile = new ProfileMethod((byte) 0, 0, 0);
        profile.markBeforeTime(activeObject.getStartTimeMillis());

        activeObject.getProfileCallStack().push(profile);
    }

    public void end(MethodInfo methodInfo, Throwable throwable)
    {
        ActiveObject activeObject = traceContext.current();
        if (activeObject == null)
        {
            return;
        }

        try
        {
//            traceRepository.save(activeObject);

            ProfileMethod profileMethod = (ProfileMethod) activeObject.getProfileCallStack().pop();
            profileMethod.markAfterTime(activeObject.getStartTimeMillis());

            logger.info("profile = {}, start time = {}, elapsed time = {}", methodInfo.getName(), profileMethod.getStartTime(), profileMethod.getElapsedTime());
        }
        catch (Throwable innerThrowable)
        {
            logger.info(innerThrowable);
        }
    }
}
