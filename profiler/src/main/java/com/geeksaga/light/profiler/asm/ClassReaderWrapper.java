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
package com.geeksaga.light.profiler.asm;

import java.io.IOException;
import java.io.InputStream;

import com.geeksaga.light.profiler.instrument.transformer.ClassFileTransformerDispatcher;
import com.geeksaga.light.profiler.util.ASMUtil;
import org.objectweb.asm.ClassReader;

/**
 * @author geeksaga
 */
public class ClassReaderWrapper extends ClassReader
{
    public ClassReaderWrapper(byte[] classBuffer)
    {
        super(classBuffer);
    }

    public ClassReaderWrapper(final String name)
    {
        super(readClass(name));
    }

    public ClassReaderWrapper(final Class clazz)
    {
        super(readClass(clazz));
    }

    public ClassReaderWrapper(final Object object)
    {
        super(readClass(object));
    }

    protected static byte[] readClass(final Class clazz)
    {
        if (clazz != null)
        {
            ClassLoader loader = clazz.getClassLoader();

            return readClass(loader, clazz.getName());
        }

        return new byte[0];
    }

    protected static byte[] readClass(final Object object)
    {
        if (object != null && object.getClass() != null)
        {
            ClassLoader loader = object.getClass().getClassLoader();

            return readClass(loader, object.getClass().getName());
        }

        return new byte[0];
    }

    protected static byte[] readClass(final String name)
    {
        return readClass(null, name);
    }

    private static byte[] readClass(ClassLoader loader, String name)
    {
        try
        {
            String internalClassName = ASMUtil.getInternalName(name);

            if (loader == null)
            {
                return readClass(ClassLoader.getSystemResourceAsStream(internalClassName + ".class"), true, internalClassName);
            }

            return readClass(loader.getResourceAsStream(internalClassName + ".class"), true, internalClassName);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }

        return new byte[0];
    }

    private static byte[] readClass(InputStream is, boolean close, String name) throws IOException
    {
        if (is == null)
        {
            throw new IOException("Class not found : " + name);
        }

        try
        {
            byte[] b = new byte[is.available()];
            int len = 0;
            while (true)
            {
                int n = is.read(b, len, b.length - len);
                if (n == -1)
                {
                    if (len < b.length)
                    {
                        byte[] c = new byte[len];
                        System.arraycopy(b, 0, c, 0, len);
                        b = c;
                    }
                    return b;
                }
                len += n;
                if (len == b.length)
                {
                    int last = is.read();
                    if (last < 0)
                    {
                        return b;
                    }
                    byte[] c = new byte[b.length + 1000];
                    System.arraycopy(b, 0, c, 0, len);
                    c[len++] = (byte) last;
                    b = c;
                }
            }
        }
        finally
        {
            if (close)
            {
                is.close();
            }
        }
    }

    public static boolean isValid(String className)
    {
        return mockRead(ClassFileTransformerDispatcher.context.get(), className);
    }

    protected static boolean mockRead(ClassLoader loader, String name)
    {
        try
        {
            String internalClassName = ASMUtil.getInternalName(name);

            if (loader == null)
            {
                return mockRead(ClassLoader.getSystemResourceAsStream(internalClassName + ".class"));
            }

            return mockRead(loader.getResourceAsStream(internalClassName + ".class"));
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }

        return false;
    }

    private static boolean mockRead(InputStream is)
    {
        return is != null;
    }
}