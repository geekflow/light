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

import org.slf4j.Logger;

/**
 * @author geeksaga
 */
public class SLF4JLoggerAdapter implements LightLogger {

    private final Logger logger;

    public SLF4JLoggerAdapter(Logger logger) {
        this.logger = logger;
    }

    public String getName() {
        return logger.getName();
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void info(Object obj) {
        if(obj != null) {
            logger.info(obj.toString());
        }
    }

    @Override
    public void info(Throwable throwable) {
        if(throwable != null) {
            info(CommonLogger.getStackTrace(throwable));
        }
    }

    @Override
    public void info(StackTraceElement[] stackTraceElements) {
        if(stackTraceElements != null) {
            info(CommonLogger.getStackTrace(stackTraceElements));
        }
    }
}
