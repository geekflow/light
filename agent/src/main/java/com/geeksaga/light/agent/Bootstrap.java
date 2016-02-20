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
import java.util.concurrent.*;
import java.util.jar.JarFile;
import java.util.logging.Level;
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

        instrumentation.addTransformer(new LightClassFileTransformer());

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
                // ClassLoader classLoader = new AgentClassLoader("", Bootstrap.class.getClassLoader());

                // Class.forName("", true, classLoader).newInstance();

                return true;
            }
        });

        executorService.submit(future);
    }

    public String getOptions()
    {
        return options;
    }


    private void appendToBootstrapClassLoaderSearch(JarFile jarFile)
    {
        instrumentation.appendToBootstrapClassLoaderSearch(jarFile);
//        instrumentation.appendToBootstrapClassLoaderSearch(new JarFile("./lib/asm.5.0.4.jar"));
    }

    private void failInitialize() {
        System.err.println("***********************************************************");
        System.err.println("* Light Agent Initialize failure");
        System.err.println("***********************************************************");
    }
}
