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
package com.geeksaga.light.test;

import com.geeksaga.light.config.Config;
import com.geeksaga.light.profiler.config.ProfilerConfiguration;
import com.geeksaga.light.repository.Product;
import com.geeksaga.light.repository.connect.RepositoryConnection;
import com.geeksaga.light.repository.connect.RepositorySource;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;

import java.io.File;

import static com.geeksaga.light.agent.config.ConfigDef.instance_id;
import static com.geeksaga.light.agent.config.ConfigDefaultValueDef.default_instance_id;

/**
 * @author geeksaga
 */
public class TestConfigure
{
    //    private static ThreadLocal<RepositoryConnection> repositoryConnection = new ThreadLocal<RepositoryConnection>();
    private static RepositorySource repositorySource;
    private static RepositoryConnection repositoryConnection;

    public static void load()
    {
        System.setProperty("light.config", System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "light.conf");
        System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "log4j2.xml");

        System.setProperty("light.db.url", String.format("memory:%s/", Product.NAME.toLowerCase()));
//        System.setProperty("light.db.url", String.format("plocal:../databases/%s/", Product.NAME.toLowerCase()));
    }

    public static RepositorySource getRepositorySource()
    {
        if(repositorySource == null)
        {
            repositorySource = new RepositorySource(getConfig(), read(getConfig(), instance_id, default_instance_id));
        }

        return repositorySource;
    }

    public static RepositoryConnection getConnection()
    {
        if (repositoryConnection == null)
        {
//            repositoryConnection = new RepositoryConnection(getConfig(), read(getConfig(), instance_id, default_instance_id));
        }

        return repositoryConnection;
    }

    public static Config getConfig()
    {
        return ProfilerConfiguration.load(TestConfigure.class.getClassLoader(), "light.conf");
    }

    public static String read(Config config, String key, short defaultValue)
    {
        return String.valueOf(config.read(key, defaultValue));
    }
}