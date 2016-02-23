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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author geeksaga
 */
public class AgentClassLoader extends URLClassLoader {
    public AgentClassLoader(String classPath, ClassLoader parent) {
        super(toURLs(classPath), parent);
    }

    public AgentClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    private static URL[] toURLs(String classPath) {
        try {
            List<URL> urlList = new ArrayList<URL>();
            StringTokenizer stringTokenizer = new StringTokenizer(classPath, ";");
            while (stringTokenizer.hasMoreTokens()) {
                String path = stringTokenizer.nextToken();
                path = path.trim();
                if (path.length() > 0) {
                    URL url = toURLOrNullIfOccurException(path);

                    if (url != null) {
                        urlList.add(url);
                    }
                }
            }

            return urlList.toArray(new URL[urlList.size()]);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return new URL[0];
    }

    private static URL toURLOrNullIfOccurException(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                return file.toURI().toURL();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }
}
