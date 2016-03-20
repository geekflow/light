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
package com.geeksaga.light.agent;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * @author geeksaga
 */
public class JavaAgent {
    private static final Logger logger = Logger.getLogger(JavaAgent.class.getName());

    private static final boolean STATUS_NONE = false;
    private static final boolean STATUS_LOADED = true;

    protected static final AtomicBoolean STATUS = new AtomicBoolean(STATUS_NONE);

    private static Instrumentation instrumentation;

    public static void premain(String options, Instrumentation instrumentation) {
        init(options, instrumentation, false);
    }

    public static void agentmain(String options, Instrumentation instrumentation) {
        init(options, instrumentation, true);
    }

    private static void init(String options, Instrumentation instrumentation, boolean attach) {
        if (updateStatusAndCheckDuplicate()) {
            failInitialize();
            return;
        }

        JavaAgent.instrumentation = instrumentation;

        Bootstrap bootstrap = new Bootstrap(options, instrumentation);
        bootstrap.initialize(attach);
    }

    private static boolean updateStatusAndCheckDuplicate() {
        final boolean loadSuccess = STATUS.compareAndSet(STATUS_NONE, STATUS_LOADED);
        if (loadSuccess) {
            return false;
        } else {
            logger.warning("Light Agent already initialized. Skip agent loading.");
            return true;
        }
    }

    private static void failInitialize() {
        System.err.println("***********************************************************");
        System.err.println("* Light Agent Initialize failure");
        System.err.println("***********************************************************");
    }
}
