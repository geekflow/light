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
package com.geeksaga.light.tools.util;

import javax.tools.ToolProvider;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author geeksaga
 */
public class ToolsLoader
{
    private static final Logger logger = Logger.getLogger(ToolsLoader.class.getName());

    private static ClassLoader toolsLoader;

    public static ClassLoader getLoader(ClassLoader parent)
    {
        return getLoader(parent, null);
    }

    public static ClassLoader getLoader(ClassLoader parent, String path)
    {
        if (toolsLoader == null)
        {
            try
            {
                toolsLoader = new URLClassLoader(getURL(path), parent);
            }
            catch (Exception exception)
            {
                logger.log(Level.INFO, exception.getMessage(), exception);
            }
        }

        return toolsLoader;
    }

    public static void setToolsLoader(ClassLoader toolsLoader)
    {
        ToolsLoader.toolsLoader = toolsLoader;
    }

    private static URL[] getURL(String path) throws MalformedURLException
    {
        if (path != null)
        {
            return new URL[] { getToolsFile(), new File(path).toURI().toURL() };
        }

        return new URL[] { getToolsFile() };
    }

    private static URL getToolsFile()
    {
        URL[] urls = ((URLClassLoader) ToolProvider.getSystemToolClassLoader()).getURLs();

        for (URL url : urls)
        {
            if (url.getFile().contains("tools.jar"))
            {
                return url;
            }
        }

        return null;
    }
}
