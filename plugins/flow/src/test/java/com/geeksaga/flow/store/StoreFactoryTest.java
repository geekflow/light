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
package com.geeksaga.flow.store;

import com.geeksaga.light.util.SystemProperty;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author geeksaga
 */
public class StoreFactoryTest
{
    private static StoreFactory factory = null;
    private static final String DEFAULT_PATH = "/../../databases/";

    @BeforeClass
    public static void init()
    {
        System.setProperty("flow.db.path", String.format("plocal:%s%s", System.getProperty("user.dir"), replaceWindowsSeparator(DEFAULT_PATH)));

        factory = StoreFactory.getInstance("flowtest");
    }

    private static String replaceWindowsSeparator(String path)
    {
        if (SystemProperty.WINDOWS_OS && path != null)
        {
            return path.replace("\\", File.separator);
        }

        return path;
    }

    @Test
    public void testOSSeparator()
    {
        if(SystemProperty.WINDOWS_OS)
        {
            assertThat(DEFAULT_PATH, is(replaceWindowsSeparator(DEFAULT_PATH.replace("/", "\\"))));
        }

        assertThat(DEFAULT_PATH, is(replaceWindowsSeparator(DEFAULT_PATH)));
    }

    @Test
    public void testGetInstance()
    {
        StoreFactory factory = StoreFactory.getInstance("flowtest");

        assertThat(factory, is(StoreFactory.getInstance()));
    }

    @Test
    public void testStore()
    {
        assertThat(factory.store(StoreFactoryTest.class.getName(), "name", StoreFactoryTest.class.getName()), is(true));
        assertThat(factory.store(StoreFactoryTest.class.getName(), "name", StoreFactoryTest.class.getName()), is(true));
        assertThat(factory.store(StoreFactoryTest.class.getName(), "name", StoreFactoryTest.class.getName()), is(true));
    }
}
