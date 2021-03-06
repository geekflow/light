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

/**
 * @author geeksaga
 */
public interface LightLogger
{
    void trace(String message);

    void trace(String format, Object... arguments);

    void trace(Object obj);

    void trace(Throwable throwable);

    void trace(StackTraceElement[] stackTraceElements);

    void debug(String message);

    void debug(String format, Object... arguments);

    void debug(Object obj);

    void debug(Throwable throwable);

    void debug(StackTraceElement[] stackTraceElements);

    void info(String message);

    void info(String format, Object... arguments);

    void info(Object obj);

    void info(Throwable throwable);

    void info(StackTraceElement[] stackTraceElements);

    void warn(String message);

    void warn(String format, Object... arguments);

    void warn(Object obj);

    void warn(Throwable throwable);

    void warn(StackTraceElement[] stackTraceElements);

    void error(String message);

    void error(String format, Object... arguments);

    void error(Object obj);

    void error(Throwable throwable);

    void error(StackTraceElement[] stackTraceElements);
}
