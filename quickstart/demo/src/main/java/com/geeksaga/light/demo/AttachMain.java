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

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author geeksaga
 */
public class AttachMain {
    private static final Logger logger = Logger.getLogger(AttachMain.class.getName());

    public void attach() {
        URL url = Main.class.getResource("/");
        if (url != null) {
            System.out.println(url.getFile());
        }

        ClassLoader loader = ToolsLoader.getLoader(Object.class.getClassLoader());

        try {
            if (loader != null) {
                logger.info(loader.toString());

                URL[] urls = ((URLClassLoader) loader).getURLs();
                for (URL u : urls) {
                    logger.info(u.toString());
                }
            }

            Class clazz = Class.forName("com.geeksaga.light.demo.VMAttach", true, loader);

//            VMAttach main = (VMAttach) clazz.newInstance();
            Object main = clazz.newInstance();

            if (main != null) {
                logger.info(getThisJarName());

                Method method = clazz.getDeclaredMethod("loadAgent", String.class);
                method.invoke(main, getAgentJarName());

//                 main.loadAgent(getThisJarName());
            }
        } catch (Exception exception) {
            logger.log(Level.INFO, exception.getMessage(), exception);
        }
    }

    public String getThisJarName() {
        String path;
        ClassLoader classLoader = Main.class.getClassLoader();
        if (classLoader == null) {
            path = "" + ClassLoader.getSystemClassLoader().getResource(Main.class.getName().replace('.', '/') + ".class");
        } else {
            path = "" + classLoader.getResource(Main.class.getName().replace('.', '/') + ".class");
        }

        if (path.contains("!")) {
            path = path.substring("jar:file:/".length(), path.indexOf("!"));
        }

        if (path.indexOf(':') > 0) {
            return path;
        }

        return "/" + path;
    }

    public String getAgentJarName() {
        String jar;
        ClassLoader classLoader = Main.class.getClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }

        URL url = classLoader.getResource(Main.class.getName().replace('.', '/') + ".class");
        jar = url.toString().replace("jar:file:", "");
        jar = jar.substring(0, jar.indexOf(".jar!") + 4);

        // return jar;
        return System.getProperty("user.dir") + File.separator + "light.agent-0.0.1.jar";
    }
}