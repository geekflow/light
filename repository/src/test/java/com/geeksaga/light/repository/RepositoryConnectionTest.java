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
import com.geeksaga.light.repository.connect.RepositorySource;
import com.geeksaga.light.repository.entity.Transaction;
import com.geeksaga.light.test.TestConfigure;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.junit.AfterClass;
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
//    private static RepositoryConnection repositoryConnection;
    private static RepositorySource repositorySource;

    @BeforeClass
    public static void init()
    {
        TestConfigure.load();

//        repositoryConnection = TestConfigure.getConnection();

//        repositorySource = TestConfigure.getRepositorySource();
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
//        OClass transactionClass = repositoryConnection.findClass(Transaction.class.getSimpleName());
//        OClass transactionClass = repositorySource.findClass(Transaction.class.getSimpleName());

//        assertThat(transactionClass, notNullValue());
//        assertThat(transactionClass.existsProperty("tid"), is(true));
    }

    @AfterClass
    public static void destroy()
    {
//        System.out.println("call destroy");

//        ODatabaseRecordThreadLocal.INSTANCE.set((ODatabaseDocumentInternal)repositoryConnection.activateOnCurrentThread());
//        ODatabaseRecordThreadLocal.INSTANCE.set((ODatabaseDocumentInternal)repositorySource.activateOnCurrentThread());
//        ODatabaseDocumentInternal database = ODatabaseRecordThreadLocal.INSTANCE.getIfDefined();
//        if (database != null)
//        {
//            System.out.println("call destroy");
//
//            database.close();
//            ODatabaseRecordThreadLocal.INSTANCE.remove();
//        }
//        repositoryConnection.close();
//        ODatabaseRecordThreadLocal.INSTANCE.remove();
    }
}