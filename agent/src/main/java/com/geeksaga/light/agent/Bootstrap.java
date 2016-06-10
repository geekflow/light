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

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author geeksaga
 */
public class Bootstrap
{
    private static final Logger logger = Logger.getLogger(Bootstrap.class.getName());

    private static final String DEFAULT_PROFILE_MODULE = "com.geeksaga.light.profiler.ProfilerModule";

    private final String options;
    private final Instrumentation instrumentation;

    public Bootstrap(String options, Instrumentation instrumentation)
    {
        this.options = options;
        this.instrumentation = instrumentation;
    }

    public void initialize(boolean attach)
    {
        initialize(attach, null);
    }

    public void initialize(boolean attach, String classPath)
    {
        logger.info("initialize..." + " (attach : " + attach + ")");

        if(options != null)
        {
            logger.info("javaagent options : " + options);
        }

        final AgentClassPathResolver classPathResolver = createResolver(attach, classPath);

        //
        if (!classPathResolver.isInitialize())
        {
            failInitialize();
        }

        //
        final String agentCoreJarName = classPathResolver.getAgentCoreJarName();
        if (agentCoreJarName == null)
        {
            logger.info("light.agent.core-x.x.x(-SNAPSHOT).jar not found.");

            failInitialize();

            return;
        }

        appendToBootstrapClassLoaderSearch(classPathResolver.getJarFileOrNull(classPathResolver.getAgentCoreJarAbsoluteName()));

        List<URL> urlList = classPathResolver.findAllAgentLibrary();

        //        System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "config/log4j2.xml");

        ClassLoader classLoader = new AgentClassLoader(urlList.toArray(new URL[urlList.size()]), Bootstrap.class.getClassLoader());

        try
        {
            Class<?> profilerModule = Class.forName(DEFAULT_PROFILE_MODULE, true, classLoader);
            Constructor<?> constructor = profilerModule.getConstructor(Instrumentation.class);
            Module profiler = (Module) constructor.newInstance(instrumentation);
            profiler.start();

            logger.info("initialize success.");
        }
        catch (Exception exception)
        {
            logger.log(Level.INFO, exception.getMessage(), exception);
        }
    }

    private AgentClassPathResolver createResolver(boolean attach)
    {
        return createResolver(attach, null);
    }

    private AgentClassPathResolver createResolver(boolean attach, String classPath)
    {
        if (attach)
        {
            return new AgentClassPathResolver(getAgentJarPathOrEmpty() + ((classPath != null) ? File.pathSeparator + classPath : ""));
        }

        return new AgentClassPathResolver();
    }

    private String getAgentJarPathOrEmpty()
    {
        ClassLoader classLoader = Bootstrap.class.getClassLoader();
        if (classLoader == null)
        {
            classLoader = ClassLoader.getSystemClassLoader();
        }

        URL url = classLoader.getResource(Bootstrap.class.getName().replace('.', '/') + ".class");
        String urlString = (url != null) ? url.toString() : "";

        String jarPath = "";

        if (urlString.contains("jar:file:"))
        {
            jarPath = urlString.replace("jar:file:", "");
            jarPath = jarPath.substring(0, jarPath.indexOf(".jar!") + 4);
        }

        return jarPath;
    }

    private void appendToBootstrapClassLoaderSearch(JarFile jarFile)
    {
        instrumentation.appendToBootstrapClassLoaderSearch(jarFile);
    }

    private void failInitialize()
    {
        logger.log(Level.ALL, "***********************************************************");
        logger.log(Level.ALL, "* Light Agent Initialize failure");
        logger.log(Level.ALL, "***********************************************************");
    }
}
