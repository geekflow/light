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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author geeksaga
 */
class LoadClassCommand extends AbstractCommand
{
    private String command = null;

    LoadClassCommand(String command)
    {
        this.command = command;
    }

    public boolean execute()
    {
        try
        {
            //            load(command);

            File file = new File(System.getProperty("user.dir") + File.separator + ".." + File.separator + "lib" + File.separator + "geeksaga.flow-0.1-SNAPSHOT.jar");

            loadJarFile(file, command.split(" ")[1]);

            Analysis.analysis(JarHandler.findJar(file, command.split(" ")[1]), true);
        }
        catch (Exception exception)
        {
            logger.info(exception);
        }

        return true;
    }

    public static byte[] load(String fileName) throws Exception
    {
        //        FileInputStream is = new FileInputStream(new File(Main.class.getResource("/" + fileName).toURI()));
        logger.info(fileName);

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try(InputStream is = LoadClassCommand.class.getResourceAsStream(fileName))
        {
            while (true)
            {
                byte[] buf = new byte[1024];
                int read = is.read(buf);
                if (read == -1)
                {
                    break;
                }
                os.write(buf, 0, read);
            }
        }

        return os.toByteArray();
    }

    // full path name
    private void loadJarFile(File file, String name)
    {
        logger.info(file);
        logger.info(name);

        if (file != null && file.exists())
        {
            try (JarFile jarFile = new JarFile(file))
            {
                ZipEntry ent = jarFile.getEntry(name);
                if (ent != null)
                {
                    logger.info(ent);

                    try (InputStream fin = jarFile.getInputStream(ent))
                    {
                        //                        load(fin);
                        fin.close();
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
