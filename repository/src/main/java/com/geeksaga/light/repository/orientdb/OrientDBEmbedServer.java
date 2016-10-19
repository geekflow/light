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
package com.geeksaga.light.repository.orientdb;

import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

import java.io.File;

import static com.geeksaga.light.util.SystemProperty.LIGHT_REPOSITORY_CONFIG;
import static com.geeksaga.light.util.SystemProperty.ORIENTDB_HOME;

/**
 * @author geeksaga
 */
public class OrientDBEmbedServer
{
    private final LightLogger logger;
    private OServer server;

    public OrientDBEmbedServer()
    {
        this.logger = CommonLogger.getLogger(getClass().getName());

        init();
    }

    public void init()
    {
        setOrientDbHome();

        try
        {
            server = OServerMain.create();
        }
        catch (Exception exception)
        {
            logger.info(exception);
        }
    }

    public void startup()
    {
        startup(LIGHT_REPOSITORY_CONFIG);
    }

    public void startup(String configFilePath)
    {
        try
        {
            server.startup(new File(configFilePath));
            server.activate();
        }
        catch (Exception e)
        {
            logger.info(e);
        }
    }

    private void setOrientDbHome()
    {
        if (ORIENTDB_HOME.length() == 0)
        {
            System.setProperty(Orient.ORIENTDB_HOME, new File("").getAbsolutePath()); //Set OrientDB home to current directory
        }
    }

    public boolean shutdown()
    {
        return server.shutdown();
    }

    public boolean isActive()
    {
        return server.isActive();
    }


}