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

import com.geeksaga.light.repository.connect.RepositoryConnection;
import com.geeksaga.light.repository.entity.Transaction;
import com.geeksaga.light.test.TestConfigure;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author geeksaga
 */
public class RepositoryConnectionTest
{
    private static RepositoryConnection repositoryConnection;

    @BeforeClass
    //    @Before
    public static void init()
    {
        TestConfigure.load();

        //        Config config = TestConfigure.getConfig();

        //        repositoryConnection = new RepositoryConnection(config, TestConfigure.read(config, instance_id, default_instance_id) + "R");
        repositoryConnection = TestConfigure.getConnection();
    }

    @Test
    public void testGetSameInstance()
    {
        //        assertThat(repositoryConnection, is(RepositoryConnection.getInstance()));
        //        assertThat(repositoryConnection.getObjectDatabaseTx(), is(RepositoryConnection.getInstance().getObjectDatabaseTx()));
    }

    @Test
    public void testFindClass()
    {
        OClass transactionClass = repositoryConnection.findClass(Transaction.class.getSimpleName());

        assertThat(transactionClass, notNullValue());
        assertThat(transactionClass.existsProperty("tid"), is(true));
    }
}