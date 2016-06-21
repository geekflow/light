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
package com.geeksaga.light.repository;

import com.geeksaga.light.repository.store.StoreFactory;
import com.geeksaga.light.util.SystemProperty;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author geeksaga
 */
public class StoreFactoryTest
{
    private static StoreFactory factory;
    private static final String DEFAULT_PATH = "/../databases/";

    @BeforeClass
    public static void init()
    {
        System.setProperty("light.db.path", String.format("plocal:%s%s", System.getProperty("user.dir"), replaceWindowsSeparator(DEFAULT_PATH)));

        factory = StoreFactory.getInstance(Product.NAME + "Test");
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
        if (SystemProperty.WINDOWS_OS)
        {
            assertThat(DEFAULT_PATH, is(replaceWindowsSeparator(DEFAULT_PATH.replace("/", "\\"))));
        }

        assertThat(DEFAULT_PATH, is(replaceWindowsSeparator(DEFAULT_PATH)));
    }

    @Test
    public void testGetInstance()
    {
        assertThat(factory, is(StoreFactory.getInstance(Product.NAME + "Test")));
    }

    @Test
    public void testFindClass()
    {
        OClass oClass = factory.findClass("transaction");

        assertThat(oClass, notNullValue());

        assertThat(oClass.existsProperty("id"), is(true));
    }


    @Test
    public void testStore()
    {
        //        assertThat(factory.store(StoreFactoryTest.class.getName(), "name", StoreFactoryTest.class.getName()), is(true));
        //        assertThat(factory.store(StoreFactoryTest.class.getName(), "name", StoreFactoryTest.class.getName()), is(true));
        //        assertThat(factory.store(StoreFactoryTest.class.getName(), "name", StoreFactoryTest.class.getName()), is(true));
    }
}
