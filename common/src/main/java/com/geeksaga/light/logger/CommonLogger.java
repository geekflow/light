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
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author geeksaga
 */
public class CommonLogger {
    private ConcurrentMap<String, LightLogger> LOGGER_CACHE = new ConcurrentHashMap<String, LightLogger>(256, 075f, 128);

    private LightLogger impl = new LightLogger() {
        @Override
        public void info(String message) {
            System.out.println(message);
        }

        @Override
        public void info(Object obj) {
            if (obj != null) {
                info(obj.toString());
            }
        }

        @Override
        public void info(Throwable throwable) {
            info(getStackTrace(throwable));
        }

        @Override
        public void info(StackTraceElement[] stackTraceElements) {
            info(getStackTrace(stackTraceElements));
        }
    };

    public LightLogger getLogger(String name) {
        final LightLogger logger = LOGGER_CACHE.get(name);

        if (logger != null) {
            return logger;
        }

        final SLF4JLoggerAdapter slf4JLoggerAdapter = new SLF4JLoggerAdapter(LoggerFactory.getLogger(name));

        final LightLogger slf4JLogger = LOGGER_CACHE.putIfAbsent(name, slf4JLoggerAdapter);
        if(slf4JLogger != null) {
            return slf4JLogger;
        }

        return impl;
    }

    public static String getStackTrace(StackTraceElement[] stackTraceElements) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(SystemProperty.LINE_SEPARATOR);

        for (StackTraceElement stackTraceElement : stackTraceElements) {
            sb.append("\tat ").append(stackTraceElement).append(SystemProperty.LINE_SEPARATOR);
        }

        return sb.toString();
    }

    public static String getStackTrace(Throwable throwable) {
        if (throwable != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            printWriter.close();

            return stringWriter.toString();
        }

        return "";
    }
}
