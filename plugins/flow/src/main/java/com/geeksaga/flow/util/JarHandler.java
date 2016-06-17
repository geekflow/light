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
package com.geeksaga.flow.util;

import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author geeksaga
 */
public class JarHandler
{
    private static final LightLogger logger = CommonLogger.getLogger(JarHandler.class.getName());

    public static byte[] findJar(File file, String name) throws IOException
    {
        return getJarEntry(new JarFile(file), name);
    }

    public static byte[] getJarEntry(JarFile jar, String className)
    {
        try (InputStream in = getJarEntryAsStream(jar, className))
        {
            if (in != null)
            {
                return readAll(in);
            }
        }
        catch (IOException e)
        {
            logger.info(e);
        }

        return null;
    }

    public static InputStream getJarEntryAsStream(JarFile jar, String name)
    {
        try
        {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements())
            {
                JarEntry entry = entries.nextElement();
                String s = entry.getName();
                if (s.equals(name))
                {
                    return jar.getInputStream(entry);
                }
            }
        }
        catch (IOException e)
        {
            logger.info(e);
        }

        return null;
    }

    public static byte[] readAll(InputStream stream) throws IOException
    {
        if (stream != null)
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int n = stream.read(buff);
            while (n >= 0)
            {
                out.write(buff, 0, n);
                n = stream.read(buff);
            }

            return out.toByteArray();
        }

        return null;
    }
}
