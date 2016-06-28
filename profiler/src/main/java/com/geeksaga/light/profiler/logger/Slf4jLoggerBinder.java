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

import com.geeksaga.light.logger.LightLogger;
import com.geeksaga.light.logger.LightLoggerBinder;
//import com.geeksaga.light.logger.SLF4JLoggerAdapter;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author geeksaga
 */
public class Slf4jLoggerBinder implements LightLoggerBinder
{
    private static ConcurrentMap<String, LightLogger> LOGGER_CACHE = new ConcurrentHashMap<String, LightLogger>(256, 075f, 128);

    @Override
    public LightLogger getLogger(String name)
    {
        final LightLogger logger = LOGGER_CACHE.get(name);

        if (logger != null)
        {
            return logger;
        }

        final SLF4JLoggerAdapter slf4JLoggerAdapter = new SLF4JLoggerAdapter(LoggerFactory.getLogger(name));

        final LightLogger slf4JLogger = LOGGER_CACHE.putIfAbsent(name, slf4JLoggerAdapter);
        if (slf4JLogger != null)
        {
            return slf4JLogger;
        }

        return slf4JLoggerAdapter;
    }
}
