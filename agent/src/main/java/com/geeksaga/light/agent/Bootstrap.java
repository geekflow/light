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

import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * @author geeksaga
 */
public class Bootstrap {
    private static final Logger logger = Logger.getLogger(Bootstrap.class.getName());

    public void initialize() {
        logger.info("initialize...");

        ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactory()
        {
            public Thread newThread(Runnable runnable)
            {
                Thread thread = new Thread(runnable);
                thread.setName("Light-Bootstrap ");
                thread.setDaemon(true);

                return thread;
            }
        });

        FutureTask<Boolean> future = new FutureTask<Boolean>(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                // ClassLoader classLoader = new AgentClassLoader("", Bootstrap.class.getClassLoader());

                // Class.forName("", true, classLoader).newInstance();

                return true;
            }
        });

        executorService.submit(future);
    }
}
