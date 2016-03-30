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
package com.geeksaga.light.agent.logger;

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
            info(throwable.getStackTrace());
        }

        @Override
        public void info(StackTraceElement[] stackTraceElements) {

        }
    };

    public LightLogger getLogger(String name) {
        final LightLogger logger = LOGGER_CACHE.get(name);

        if (logger != null) {
            return logger;
        }

        // FIXME SLF4J bind

        return impl;
    }
}
