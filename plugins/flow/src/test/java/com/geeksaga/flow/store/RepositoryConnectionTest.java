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

import com.geeksaga.flow.Product;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author geeksaga
 */
public class RepositoryConnectionTest
{
    private static RepositoryFactory factory;

    @BeforeClass
    public static void init()
    {
        System.setProperty("flow.db.path", String.format("memory:/%s/", Product.NAME.toUpperCase()));

        factory = RepositoryFactory.getInstance("flowtest");
    }

    @Test
    public void testGetInstance()
    {
        RepositoryFactory factory = RepositoryFactory.getInstance("flowtest");

        assertThat(factory, is(RepositoryFactory.getInstance()));
    }

    @Test
    public void testStore()
    {
        assertThat(factory.store(RepositoryConnectionTest.class.getName(), "name", RepositoryConnectionTest.class.getName()), is(true));
        assertThat(factory.store(RepositoryConnectionTest.class.getName(), "name", RepositoryConnectionTest.class.getName()), is(true));
        assertThat(factory.store(RepositoryConnectionTest.class.getName(), "name", RepositoryConnectionTest.class.getName()), is(true));
    }
}
