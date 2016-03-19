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
package com.geeksaga.light.demo;

import javax.tools.ToolProvider;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author geeksaga
 */
public class ToolsLoader {
    private static ClassLoader toolsLoader;

    public static ClassLoader getLoader(ClassLoader parent) {
        if (toolsLoader == null) {
            try {
                toolsLoader = new URLClassLoader(new URL[]{getToolsFile(), new File(System.getProperty("user.dir") + File.separator + "light.demo-0.0.1.jar").toURI().toURL()}, parent);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        return toolsLoader;
    }

    public static URL getToolsFile() {
        URL[] urls = ((URLClassLoader) ToolProvider.getSystemToolClassLoader()).getURLs();

        for (URL url : urls) {
            if (url.getFile().contains("tools.jar")) {
                return url;
            }
        }

        return null;
    }
}
