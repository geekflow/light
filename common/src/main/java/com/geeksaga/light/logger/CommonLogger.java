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
package com.geeksaga.light.logger;

import com.geeksaga.light.util.SystemProperty;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author geeksaga
 */
public class CommonLogger
{
    private static LightLoggerBinder loggerBinder;

    public static void initialize(LightLoggerBinder loggerBinder)
    {
        CommonLogger.loggerBinder = loggerBinder;
    }

    public static LightLogger getLogger(String name)
    {
        if (loggerBinder == null)
        {
            return new LightLogger()
            {
                @Override
                public void trace(String message)
                {}

                @Override
                public void trace(String format, Object... arguments)
                {}

                @Override
                public void trace(Object obj)
                {}

                @Override
                public void trace(Throwable throwable)
                {}

                @Override
                public void trace(StackTraceElement[] stackTraceElements)
                {}

                @Override
                public void debug(String message)
                {}

                @Override
                public void debug(String format, Object... arguments)
                {}

                @Override
                public void debug(Object obj)
                {}

                @Override
                public void debug(Throwable throwable)
                {}

                @Override
                public void debug(StackTraceElement[] stackTraceElements)
                {}

                @Override
                public void info(String message)
                {}

                @Override
                public void info(String format, Object... arguments)
                {}

                @Override
                public void info(Object obj)
                {}

                @Override
                public void info(Throwable throwable)
                {}

                @Override
                public void info(StackTraceElement[] stackTraceElements)
                {}

                @Override
                public void warn(String message)
                {}

                @Override
                public void warn(String format, Object... arguments)
                {}

                @Override
                public void warn(Object obj)
                {}

                @Override
                public void warn(Throwable throwable)
                {}

                @Override
                public void warn(StackTraceElement[] stackTraceElements)
                {}

                @Override
                public void error(String message)
                {}

                @Override
                public void error(String format, Object... arguments)
                {}

                @Override
                public void error(Object obj)
                {}

                @Override
                public void error(Throwable throwable)
                {}

                @Override
                public void error(StackTraceElement[] stackTraceElements)
                {}
            };
        }

        return loggerBinder.getLogger(name);
    }

    public static String getStackTrace(StackTraceElement[] stackTraceElements)
    {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(SystemProperty.LINE_SEPARATOR);

        for (StackTraceElement stackTraceElement : stackTraceElements)
        {
            sb.append("\tat ").append(stackTraceElement).append(SystemProperty.LINE_SEPARATOR);
        }

        return sb.toString();
    }

    public static String getStackTrace(Throwable throwable)
    {
        if (throwable != null)
        {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            printWriter.close();

            return stringWriter.toString();
        }

        return "";
    }
}
