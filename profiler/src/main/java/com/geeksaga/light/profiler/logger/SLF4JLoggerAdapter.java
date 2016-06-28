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
package com.geeksaga.light.profiler.logger;

import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import org.slf4j.Logger;

/**
 * @author geeksaga
 */
public class SLF4JLoggerAdapter implements LightLogger
{
    private final Logger logger;

    public SLF4JLoggerAdapter(Logger logger)
    {
        this.logger = logger;
    }

    public String getName()
    {
        return logger.getName();
    }

    @Override
    public void trace(String message)
    {
        logger.trace(message);
    }

    @Override
    public void trace(String format, Object... arguments)
    {
        logger.trace(format, arguments);
    }

    @Override
    public void trace(Object obj)
    {
        if (obj != null)
        {
            trace(obj.toString());
        }
    }

    @Override
    public void trace(Throwable throwable)
    {
        if (throwable != null)
        {
            trace(CommonLogger.getStackTrace(throwable));
        }
    }

    @Override
    public void trace(StackTraceElement[] stackTraceElements)
    {
        if (stackTraceElements != null)
        {
            trace(CommonLogger.getStackTrace(stackTraceElements));
        }
    }

    @Override
    public void debug(String message)
    {
        logger.debug(message);
    }

    @Override
    public void debug(Object obj)
    {
        if (obj != null)
        {
            logger.debug(obj.toString());
        }
    }

    @Override
    public void debug(String format, Object... arguments)
    {
        logger.debug(format, arguments);
    }

    @Override
    public void debug(Throwable throwable)
    {
        if (throwable != null)
        {
            debug(CommonLogger.getStackTrace(throwable));
        }
    }

    @Override
    public void debug(StackTraceElement[] stackTraceElements)
    {
        if (stackTraceElements != null)
        {
            debug(CommonLogger.getStackTrace(stackTraceElements));
        }
    }

    @Override
    public void info(String message)
    {
        logger.info(message);
    }

    @Override
    public void info(Object obj)
    {
        if (obj != null)
        {
            info(obj.toString());
        }
    }

    @Override
    public void info(String format, Object... arguments)
    {
        logger.info(format, arguments);
    }

    @Override
    public void info(Throwable throwable)
    {
        if (throwable != null)
        {
            info(CommonLogger.getStackTrace(throwable));
        }
    }

    @Override
    public void info(StackTraceElement[] stackTraceElements)
    {
        if (stackTraceElements != null)
        {
            info(CommonLogger.getStackTrace(stackTraceElements));
        }
    }

    @Override
    public void warn(String message)
    {
        logger.warn(message);
    }

    @Override
    public void warn(String format, Object... arguments)
    {
        logger.warn(format, arguments);
    }

    @Override
    public void warn(Object obj)
    {
        if (obj != null)
        {
            warn(obj.toString());
        }
    }

    @Override
    public void warn(Throwable throwable)
    {
        if (throwable != null)
        {
            warn(CommonLogger.getStackTrace(throwable));
        }
    }

    @Override
    public void warn(StackTraceElement[] stackTraceElements)
    {
        if (stackTraceElements != null)
        {
            warn(CommonLogger.getStackTrace(stackTraceElements));
        }
    }

    @Override
    public void error(String message)
    {
        logger.error(message);
    }

    @Override
    public void error(String format, Object... arguments)
    {
        logger.error(format, arguments);
    }

    @Override
    public void error(Object obj)
    {
        if (obj != null)
        {
            error(obj.toString());
        }
    }

    @Override
    public void error(Throwable throwable)
    {
        if (throwable != null)
        {
            error(CommonLogger.getStackTrace(throwable));
        }
    }

    @Override
    public void error(StackTraceElement[] stackTraceElements)
    {
        if (stackTraceElements != null)
        {
            error(CommonLogger.getStackTrace(stackTraceElements));
        }
    }
}