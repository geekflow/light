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

import com.geeksaga.light.tools.Main;
import com.geeksaga.light.tools.util.ToolsLoader;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author geeksaga
 */
public class AttachMain
{
    private static final Logger logger = Logger.getLogger(AttachMain.class.getName());

    public void attach()
    {
        URL url = Main.class.getResource("/");
        if (url != null)
        {
            System.out.println(url.getFile());
        }

        //        ClassLoader loader = ToolsLoader.getLoader(Thread.currentThread().getContextClassLoader());
        ClassLoader loader = ToolsLoader.getLoader(Object.class.getClassLoader());

        try
        {
            if (loader != null)
            {
                logger.info(loader.toString());

                URL[] urls = ((URLClassLoader) loader).getURLs();
                for (URL u : urls)
                {
                    logger.info(u.toString());
                }
            }

            Class<?> clazz = Class.forName("com.geeksaga.light.tools.vm.VMAttach", false, loader);

            Object main = clazz.newInstance();
            if (main != null)
            {
                logger.info(getThisJarName());

                Method method = clazz.getDeclaredMethod("loadAgent", String.class);
                method.invoke(main, getAgentJarName());

                //                 main.loadAgent(getThisJarName());
            }
        }
        catch (Exception exception)
        {
            logger.log(Level.INFO, exception.getMessage(), exception);
        }
    }

    public void show()
    {
        if (!invoke("com.geeksaga.light.tools.vm.VMAttach", "show", Object.class.getClassLoader()))
        {
            invoke("com.geeksaga.light.tools.vm.VMAttach", "show", Thread.currentThread().getContextClassLoader());
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
                Method method = clazz.getMethod(methodName);
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
            logger.log(Level.INFO, e.getMessage(), e);
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
        String jar;
        ClassLoader classLoader = Main.class.getClassLoader();
        if (classLoader == null)
        {
            classLoader = ClassLoader.getSystemClassLoader();
        }

        URL url = classLoader.getResource(Main.class.getName().replace('.', '/') + ".class");

        if (url != null)
        {
            jar = url.toString().replace("jar:file:", "");
            jar = jar.substring(0, jar.indexOf(".jar!") + 4);

            // return jar;
        }

        return System.getProperty("user.dir") + File.separator + "light.agent-" + findLastAgentJarOrNull("./") + ".jar";
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
        catch (Exception exception)
        {
            logger.log(Level.INFO, exception.getMessage(), exception);
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