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
package com.geeksaga.light.version;

import java.net.URL;

/**
 * @author geeksaga
 */
public class RunningFromJar
{
    public static String getVersionNumberOrNull()
    {
        String filePath = getJarFilePathOrNull(RunningFromJar.class);
        if (filePath == null)
        {
            return null;
        }

        return filePath.substring((filePath.lastIndexOf("-") + 1), (filePath.length() - 4));
    }

    public static String getJarFilePathOrNull(Class<?> clazz)
    {
        URL url = clazz.getResource("/" + clazz.getName().replace('.', '/').trim() + ".class");
        String urlString = (url != null) ? url.toString() : "";

        String jarPath = null;

        if (urlString.contains("jar:file:"))
        {
            jarPath = urlString.replace("jar:file:", "");
            jarPath = jarPath.substring(0, jarPath.indexOf(".jar!") + 4);
        }

        return jarPath;
    }
}