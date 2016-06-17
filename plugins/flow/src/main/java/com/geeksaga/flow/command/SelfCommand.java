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
package com.geeksaga.flow.command;

import com.geeksaga.flow.analysis.Analysis;
import com.geeksaga.flow.util.JarHandler;

import java.io.File;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author geeksaga
 */
class SelfCommand extends AbstractCommand
{
    SelfCommand() {}

    public boolean execute()
    {
        try
        {
            File file = new File(System.getProperty("user.dir") + File.separator + ".." + File.separator + "lib" + File.separator + "geeksaga.flow-0.1-SNAPSHOT.jar");

            loadJarFile(file);
        }
        catch (Exception exception)
        {
            logger.info(exception);
        }

        return true;
    }

    // full path name
    private void loadJarFile(File file)
    {
        System.out.println(file);

        if (file != null && file.exists())
        {
            try (JarFile jarFile = new JarFile(file))
            {
                Enumeration<JarEntry> enumeration = jarFile.entries();

                while (enumeration.hasMoreElements())
                {
                    String name = enumeration.nextElement().getName();

                    if (name.endsWith(".class"))
                    {
                        Analysis.analysis(JarHandler.findJar(file, name), false);
                    }
                }
            }
            catch (Exception exception)
            {
                logger.info(exception);
            }
        }
    }
}
