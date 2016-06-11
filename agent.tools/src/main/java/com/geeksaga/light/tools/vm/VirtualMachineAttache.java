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
package com.geeksaga.light.tools.vm;

import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.geeksaga.light.tools.Main;
import com.geeksaga.light.tools.util.ToolsLoader;
import com.geeksaga.light.util.SystemProperty;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author geeksaga
 */
public class VirtualMachineAttache
{
    private static final LightLogger logger = CommonLogger.getLogger(VirtualMachineAttache.class.getName());

    private static final String ATTACH_CLASS_NAME = "com.geeksaga.light.tools.vm.VirtualMachineWrapper";

    public void loadAgentAfterAttach()
    {
        loadAgentAfterAttach(null);
    }

    public void loadAgentAfterAttach(String options)
    {
        loadAgentAfterAttach(ManagementFactory.getRuntimeMXBean().getName().split("@")[0], options);
    }

    public void loadAgentAfterAttach(String processId, String options)
    {
        if (!invoke(ATTACH_CLASS_NAME, "loadAgentAfterAttach", new Class<?>[] { String.class, String.class, String.class }, new String[] { processId, getAgentJarName(), options }, Object.class.getClassLoader()))
        {
            invoke(ATTACH_CLASS_NAME, "loadAgentAfterAttach", new Class<?>[] { String.class, String.class, String.class }, new String[] { processId, getAgentJarName(), options }, Thread.currentThread().getContextClassLoader());
        }
    }

    public void showProcessList()
    {
        if (!invoke(ATTACH_CLASS_NAME, "showProcessList", Object.class.getClassLoader()))
        {
            invoke(ATTACH_CLASS_NAME, "showProcessList", Thread.currentThread().getContextClassLoader());
        }
    }

    private boolean invoke(String className, String methodName, ClassLoader classLoader)
    {
        try
        {
            Class<?> clazz = Class.forName(className, false, ToolsLoader.getLoader(classLoader, getThisJarName()));

            Object main = clazz.newInstance();
            if (main != null)
            {
                Method method = clazz.getDeclaredMethod(methodName);
                method.invoke(main);
            }

            return true;
        }
        catch (ClassNotFoundException classNotFoundException)
        {
            ToolsLoader.setToolsLoader(null);
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            logger.info(e);
        }

        return false;
    }

    private boolean invoke(String className, String methodName, Class<?>[] parameterTypes, Object[] parameterValues, ClassLoader classLoader)
    {
        try
        {
            Class<?> clazz = Class.forName(className, false, ToolsLoader.getLoader(classLoader, getThisJarName()));

            Object main = clazz.newInstance();
            if (main != null)
            {
                Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
                method.invoke(main, parameterValues);
            }

            return true;
        }
        catch (ClassNotFoundException classNotFoundException)
        {
            ToolsLoader.setToolsLoader(null);
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            logger.info(e);
        }

        return false;
    }

    private String getThisJarName()
    {
        String path;
        ClassLoader classLoader = Main.class.getClassLoader();
        if (classLoader == null)
        {
            path = "" + ClassLoader.getSystemClassLoader().getResource(Main.class.getName().replace('.', '/') + ".class");
        }
        else
        {
            path = "" + classLoader.getResource(Main.class.getName().replace('.', '/') + ".class");
        }

        if (path.contains("!"))
        {
            path = path.substring("jar:file:/".length(), path.indexOf("!"));
        }

        if (path.indexOf(':') > 0)
        {
            return path;
        }

        return "/" + path;
    }

    private String getAgentJarName()
    {
        if (!"".equals(SystemProperty.LIGHT_HOME))
        {
            return SystemProperty.LIGHT_HOME + File.separator + "light.agent-" + findLastAgentJarOrNull(SystemProperty.LIGHT_HOME) + ".jar";
        }

        return System.getProperty("user.dir") + File.separator + "light.agent-" + findLastAgentJarOrNull("./") + ".jar";
    }

    // for Test
    String getAgentClassPath()
    {
        if (!"".equals(SystemProperty.LIGHT_HOME))
        {
            return getAgentJarName() + File.pathSeparator + SystemProperty.LIGHT_HOME + File.separator + "light.agent.core-" + findLastAgentJarOrNull(SystemProperty.LIGHT_HOME) + ".jar";
        }

        return getAgentJarName() + File.pathSeparator + System.getProperty("user.dir") + File.separator + "light.agent.core-" + findLastAgentJarOrNull("./") + ".jar";
    }

    String findLastAgentJarOrNull()
    {
        return findLastAgentJarOrNull(".");
    }

    String findLastAgentJarOrNull(String path)
    {
        try
        {
            String[] fileNames = new File(path).list(new FilenameFilter()
            {
                public boolean accept(File dir, String name)
                {
                    return (name.startsWith("light.agent" + "-") && name.endsWith(".jar"));
                }
            });

            List<String> fileList = new ArrayList<>();
            if (fileNames != null)
            {
                Collections.addAll(fileList, fileNames);
            }

            if (fileList.isEmpty())
            {
                return null;
            }

            return lastFileName(fileList);
        }
        catch (Exception e)
        {
            logger.info(e);
        }

        return null;
    }

    private String lastFileName(List<String> fileList)
    {
        String[] sortedFileNames = sort(fileList);

        String fileName = sortedFileNames[sortedFileNames.length - 1];

        return fileName.substring((fileName.lastIndexOf("-") + 1), (fileName.length() - 4)); // .jar
    }

    private String[] sort(List<String> fileList)
    {
        String[] fileNames = fileList.toArray(new String[fileList.size()]);

        Arrays.sort(fileNames, new Comparator<String>()
        {
            public int compare(String o1, String o2)
            {
                String[] tokens1 = parseToken(o1);
                String[] tokens2 = parseToken(o2);

                int commonCount = Math.min(tokens1.length, tokens2.length);

                for (int i = 0; i < commonCount; i++)
                {
                    int partCompare = Integer.parseInt(tokens1[i]) - Integer.parseInt(tokens2[i]);
                    if (partCompare != 0)
                    {
                        return partCompare;
                    }
                }

                return tokens1.length - tokens2.length;
            }

            private String[] parseToken(String fileName)
            {
                if (fileName == null)
                {
                    return null;
                }

                if (fileName.contains(".jar"))
                {
                    return toTokens(fileName.substring((fileName.lastIndexOf('-') + 1), fileName.lastIndexOf('.')));
                }

                return toTokens(fileName.substring((fileName.lastIndexOf('-') + 1)));
            }

            private String[] toTokens(String v)
            {
                return v.split("\\.");
            }
        });

        return fileNames;
    }
}