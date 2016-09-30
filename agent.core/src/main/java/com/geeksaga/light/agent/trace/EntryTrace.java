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

import com.geeksaga.light.agent.RepositoryContext;
import com.geeksaga.light.agent.TraceContext;
import com.geeksaga.light.agent.core.ActiveObject;
import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;

import java.util.Arrays;

/**
 * @author geeksaga
 */
public class EntryTrace implements Trace
{
    private LightLogger logger;
    private TraceContext traceContext;
    private RepositoryContext repositoryContext;

    public EntryTrace(TraceContext traceContext)
    {
        this.logger = CommonLogger.getLogger(getClass().getName());
        this.traceContext = traceContext;
    }

    public void begin(MethodInfo methodInfo)
    {
        try
        {
            if (traceContext.current() == null)
            {
                logger.info(methodInfo.getName() + methodInfo.getDesc());

                ActiveObject activeObject = create(methodInfo);
                activeObject.setStartTime(System.currentTimeMillis());
            }
        }
        catch (Throwable throwable)
        {
            logger.info(throwable);
        }
    }

    public void end(MethodInfo methodInfo, Throwable throwable)
    {
        try
        {
            ActiveObject activeObject = traceContext.current();

            if (activeObject != null)
            {
                logger.info("{} = {}", methodInfo.getParameter().size(), Arrays.toString(methodInfo.getParameter().getValues()));

                String name = getRequestURI(methodInfo.getParameter());

                logger.info("application = {}, start time = {}, end time = {}, elapsed time = {}", name, activeObject.getStartTime(), System.currentTimeMillis(), (System.currentTimeMillis() - activeObject.getStartTime()));
            }
        }
        catch (Throwable innerThrowable)
        {
            logger.info(innerThrowable);
        }
        finally
        {
            traceContext.remove();
        }
    }

    private ActiveObject create(MethodInfo methodInfo)
    {
        return traceContext.create(methodInfo);
    }

    private String getRequestURI(Parameter parameter)
    {
        if (parameter.size() <= 1)
        {
            return "Unknown";
        }

        Object obj = parameter.get(1);

        if (obj == null)
        {
            return "Unknown";
        }

        try
        {
            java.lang.reflect.Method method = obj.getClass().getDeclaredMethod("getRequestURI");

            return (String) method.invoke(obj);
        }
        catch (NoSuchMethodException noSuchMethodException)
        {
            //
        }
        catch (Exception exception)
        {
            logger.info(exception);
        }

        return obj.getClass().getName();
    }
}
