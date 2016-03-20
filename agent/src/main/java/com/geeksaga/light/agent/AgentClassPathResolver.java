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
 *
 */
package com.geeksaga.light.agent;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author geeksaga
 */
public class AgentClassPathResolver {
    private final Logger logger = Logger.getLogger(AgentClassPathResolver.class.getName());

    public static final String java_class_path = getSystemProperty("java.class.path");

    private static final Pattern LIGHT_AGENT_PATTERN = Pattern.compile("light\\.agent(-[0-9]+\\.[0-9]+\\.[0-9]+(\\-SNAPSHOT)?)?\\.jar");
    private static final Pattern LIGHT_AGENT_CORE_PATTERN = Pattern.compile("light\\.agent.core(-[0-9]+\\.[0-9]+\\.[0-9]+(\\-SNAPSHOT)?)?\\.jar");

    private String classPath;
    private String agentJarName;
    private String agentJarPath;
    private String agentCoreJarName;

    private boolean initialize = false;

    public AgentClassPathResolver() {
        this(java_class_path);
    }

    public AgentClassPathResolver(String classPath) {
        this.classPath = classPath;

        initialize();
    }

    public void initialize() {
        Matcher matcher = LIGHT_AGENT_PATTERN.matcher(classPath);

        if (matcher.find()) {
            agentJarName = parseAgentJar(matcher);
            agentJarPath = parseAgentJarPathOrNull();

            agentCoreJarName = findAgentCoreJarNameOrNull();

            initialize = true;
        }
    }

    public boolean isInitialize() {
        return initialize;
    }

    public List<URL> findAllAgentLibrary() {
        File libraryDirectory = new File(getAgentLibraryPath());
        if (!libraryDirectory.exists() && !libraryDirectory.isDirectory()) {
            return Collections.emptyList();
        }

        File[] files = libraryDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar") || name.endsWith(".xml");
            }
        });

        List<URL> list = new ArrayList<URL>();

        if (files != null) {
            for (File file : files) {
                URL url = toURIOrNullIfOccurException(file);
                if (url != null) {
                    list.add(url);
                }
            }
        }

        return list;
    }

    public String getAgentJarName() {
        return agentJarName;
    }

    public String getAgentJarPath() {
        return agentJarPath;
    }

    public String getAgentCoreJarName() {
        return agentCoreJarName;
    }

    public String getAgentLibraryPath() {
        return getAgentJarPath() + File.separator + "libs";
    }

    private String parseAgentJar(Matcher matcher) {
        return classPath.substring(matcher.start(), matcher.end());
    }

    private String parseAgentJarPathOrNull() {
        String[] classPaths = classPath.split(File.pathSeparator);

        for (String path : classPaths) {
            if (path.contains(agentJarName)) {
                return parseAgentJarPathOrCurrentPath(path);
            }
        }

        return null;
    }

    private String parseAgentJarPathOrCurrentPath(String path) {
        int index = path.lastIndexOf(File.separator);

        if (index == -1) {
            return "." + File.separator;
        }

        return path.substring(0, index);
    }

    private String findAgentCoreJarNameOrNull() {
        File[] files = new File(agentJarPath).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                Matcher matcher = LIGHT_AGENT_CORE_PATTERN.matcher(name);
                return matcher.matches();
            }
        });

        // FIXME If a redundant module exist to load the module in the final version.
        if (files == null || files.length == 0) {
            return null;
        } else if (files.length == 1) {
            return files[0].getAbsolutePath();
        }

        return null;
    }

    public JarFile getJarFile(String name) {
        try {
            return new JarFile(name);
        } catch (IOException ioException) {
            logger.log(Level.INFO, name + " file not found.", ioException);
        }

        return null;
    }

    private URL toURIOrNullIfOccurException(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException malformedURLException) {
            logger.log(Level.INFO, file.getName() + ".toURL(). Exception : " + malformedURLException.getMessage(), malformedURLException);
        }

        return null;
    }

    private static String getSystemProperty(String key) {
        return getSystemProperty(key, "");
    }

    private static String getSystemProperty(String key, String def) {
        try {
            return System.getProperty(key, def);
        } catch (RuntimeException exception) {
            return def;
        }
    }
}
