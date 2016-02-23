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

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.List;
import java.util.concurrent.*;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author geeksaga
 */
public class Bootstrap {
    private static final Logger logger = Logger.getLogger(Bootstrap.class.getName());

    private static final String DEFAULT_PROFILE_MODULE = "com.geeksaga.light.profiler.ProfilerModule";

    private final String options;
    private final Instrumentation instrumentation;

    public Bootstrap(String options, Instrumentation instrumentation) {
        this.options = options;
        this.instrumentation = instrumentation;
    }

    public void initialize() {
        logger.info("initialize...");

        final AgentClassPathResolver classPathResolver = new AgentClassPathResolver();

        //
        if(!classPathResolver.isInitialize()) {
            failInitialize();
        }

        //
        final String agentCoreJarName = classPathResolver.getAgentCoreJarName();
        if (agentCoreJarName == null) {
            logger.info("light.agent.core-x.x.x(-SNAPSHOT).jar not found.");

            failInitialize();

            return;
        }

        appendToBootstrapClassLoaderSearch(classPathResolver.getJarFile(classPathResolver.getAgentCoreJarName()));

        ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setName("Light-Bootstrap ");
                thread.setDaemon(true);

                return thread;
            }
        });

        FutureTask<Boolean> future = new FutureTask<Boolean>(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                List<URL> urlList = classPathResolver.findAllAgentLibrary();

                ClassLoader classLoader = new AgentClassLoader(urlList.toArray(new URL[urlList.size()]), Bootstrap.class.getClassLoader());

                Class<?> profilerModule = Class.forName(DEFAULT_PROFILE_MODULE, true, classLoader);

                try {
                    Constructor<?> constructor = profilerModule.getConstructor(Instrumentation.class);
                    Module profiler = (Module) constructor.newInstance(instrumentation);
                    profiler.start();
                } catch (Exception exception) {
                    logger.log(Level.INFO, exception.getMessage(), exception);
                }

                return true;
            }
        });

        executorService.submit(future);

        try {
            if (!future.get(3, TimeUnit.SECONDS)) {
                logger.info("initialize failure.");
            }
        } catch (Exception exception) {
            logger.log(Level.INFO, exception.getMessage(), exception);
        }

        logger.info("initialize success.");
    }

    public String getOptions() {
        return options;
    }

    private void appendToBootstrapClassLoaderSearch(JarFile jarFile) {
        instrumentation.appendToBootstrapClassLoaderSearch(jarFile);
    }

    private void failInitialize() {
        logger.log(Level.ALL, "***********************************************************");
        logger.log(Level.ALL, "* Light Agent Initialize failure");
        logger.log(Level.ALL, "***********************************************************");
    }
}
