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
package com.geeksaga.light.profiler.util;

import com.geeksaga.light.util.SystemProperty;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AccessControlException;

/**
 * @author geeksaga
 */
public class ClassFileDumper
{
    private ClassFileDumper() {}

    public static void dump(final String className, final byte[] classfileBuffer, final byte[] hookedClassFileBuffer)
    {
        dumpClass(className, hookedClassFileBuffer, classfileBuffer);
    }

    private static void dumpClass(String className, byte[] hookedClassFileBuffer, byte[] classfileBuffer)
    {
        try
        {
            StringBuilder dir = new StringBuilder("dump");
            dir.append(File.separator);

            int indexOf = className.lastIndexOf(".");
            String _className;
            String _path;

            StringBuilder path = new StringBuilder("");
            if (indexOf > -1)
            {
                path.append(className.substring(0, indexOf).replace(".", "/"));

                _className = className.substring(indexOf + 1);

                _path = path.toString();

                ensureDirectoryExist(dir.toString() + _path);
            }
            else
            {
                _className = className;
                _path = path.toString();
            }

            ensureDirectoryExist(dir.toString() + "original" + File.separator + _path);

            save(dir.toString() + "original" + File.separator + _path + File.separator + _className + ".class", classfileBuffer);

            if (hookedClassFileBuffer != null)
            {
                save(dir.toString() + _path + File.separator + _className + ".class", hookedClassFileBuffer);
            }
        }
        catch (AccessControlException accessControlException)
        {
            //            Logger.trace(LogCodeDef.D013, accessControlException.getMessage());
            accessControlException.printStackTrace();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            //            Logger.info(LogCodeDef.I001, "ClassDumpUtil.dump = " + exception.getMessage());
            //            Logger.debug(LogCodeDef.I001, exception);
        }
    }

    public static boolean ensureDirectoryExist(String directoryPath)
    {
        File file = new File(replaceWindowsSeparator(directoryPath));

        return file.exists() || file.mkdirs();
    }

    private static String replaceWindowsSeparator(String path)
    {
        if (SystemProperty.WINDOWS_OS && path != null)
        {
            return path.replace("\\", "\\\\");
        }

        return path;
    }

    public static void save(String name, byte[] buff)
    {
        try
        {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(replaceWindowsSeparator(name))));
            out.write(buff);
            out.close();
        }
        catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }
}