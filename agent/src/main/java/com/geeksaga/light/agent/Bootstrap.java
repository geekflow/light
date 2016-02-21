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
import java.util.concurrent.*;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * @author geeksaga
 */
public class Bootstrap {
    private static final Logger logger = Logger.getLogger(Bootstrap.class.getName());

    private final String options;
    private final Instrumentation instrumentation;

    public Bootstrap(String options, Instrumentation instrumentation) {
        this.options = options;
        this.instrumentation = instrumentation;
    }

    public void initialize() {
        logger.info("initialize...");

        AgentClassPathResolver classPathResolver = new AgentClassPathResolver();

        //
        classPathResolver.findAgentJar();

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
                // FIXME read to all of library in libs folder
                ClassLoader classLoader = new AgentClassLoader("light.agent-0.0.1.jar;./libs/light.profiler-0.0.1.jar;./libs/asm-all-5.0.4.jar", Bootstrap.class.getClassLoader());

                Module profiler;// = (Module) Class.forName("com.geeksaga.light.profiler.ProfilerModule", true, classLoader).newInstance();
                Class<?> profilerModule = Class.forName("com.geeksaga.light.profiler.ProfilerModule", true, classLoader);

                try {
                    Constructor<?> constructor = profilerModule.getConstructor(Instrumentation.class);
                    profiler = (Module) constructor.newInstance(instrumentation);
                    profiler.start();
                } catch (Exception exception) {
                    exception.printStackTrace();
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
            exception.printStackTrace();
        }

        logger.info("initialize success.");
    }

    public String getOptions() {
        return options;
    }


    private void appendToBootstrapClassLoaderSearch(JarFile jarFile) {
        instrumentation.appendToBootstrapClassLoaderSearch(jarFile);
        // instrumentation.appendToBootstrapClassLoaderSearch(new JarFile("./libs/asm.5.0.4.jar"));
    }

    private void failInitialize() {
        System.err.println("***********************************************************");
        System.err.println("* Light Agent Initialize failure");
        System.err.println("***********************************************************");
    }
}
