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

import com.geeksaga.light.Product;
import com.geeksaga.light.config.Config;
import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

import java.io.File;

import static com.geeksaga.light.agent.config.ConfigDef.db_path;
import static com.geeksaga.light.agent.config.ConfigDef.instance_id;
import static com.geeksaga.light.agent.config.ConfigDefaultValueDef.default_db_path;
import static com.geeksaga.light.util.SystemProperty.LIGHT_REPOSITORY_CONFIG;
import static com.geeksaga.light.util.SystemProperty.ORIENTDB_HOME;

/**
 * @author geeksaga
 */
public class OrientDBEmbedServer
{
    private final LightLogger logger;
    private OServer server;
    private Config config;

    public OrientDBEmbedServer(Config config)
    {
        this.logger = CommonLogger.getLogger(getClass().getName());
        this.config = config;

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
            System.setProperty(Orient.ORIENTDB_HOME, createOrientDbHome());

            logger.info("{} ORIENTDB_HOME : {}", Product.NAME.toUpperCase(), System.getProperty(Orient.ORIENTDB_HOME));
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

    private String createOrientDbHome()
    {
        // Set OrientDB home to current directory
        // return String.format("%s/%s", new File("").getAbsolutePath(), config.read(instance_id, Product.NAME.toLowerCase()));
        return String.format("%s", config.read(db_path, default_db_path));
    }
}
