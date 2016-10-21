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
package com.geeksaga.light.repository.dao;

import com.geeksaga.light.config.Config;
import com.geeksaga.light.repository.connect.RepositoryConnection;
import com.geeksaga.light.repository.dao.orientdb.TransactionDaoImpl;
import com.geeksaga.light.repository.entity.Transaction;
import com.geeksaga.light.repository.util.IdentifierUtils;
import com.geeksaga.light.test.TestConfigure;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static com.geeksaga.light.agent.config.ConfigDef.instance_id;
import static com.geeksaga.light.agent.config.ConfigDefaultValueDef.default_instance_id;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author geeksaga
 */
@Ignore
public class TransactionDaoTest
{
    private RepositoryConnection repositoryConnection;
    private TransactionDao transactionDao;

    //    @BeforeClass
    @Before
    public void init()
    {
        TestConfigure.load();

        final Config config = TestConfigure.getConfig();

        //        repositoryConnection = new RepositoryConnection(config, TestConfigure.read(config, instance_id, default_instance_id) + "Test");
        repositoryConnection = TestConfigure.getConnection();
        transactionDao = new TransactionDaoImpl(repositoryConnection);
    }

    @Test
    public void testSave()
    {
        OObjectDatabaseTx objectDatabaseTx = repositoryConnection.getObjectDatabaseTx();

        Transaction transaction = objectDatabaseTx.newInstance(Transaction.class, IdentifierUtils.nextLong());

        transactionDao.save(transaction);

//        if (objectDatabaseTx.isActiveOnCurrentThread())
//        {
//            objectDatabaseTx.close();
//        }
    }

    @Test(expected = com.orientechnologies.orient.core.storage.ORecordDuplicatedException.class)
    public void testUniqueIndex()
    {
        OObjectDatabaseTx objectDatabaseTx = repositoryConnection.getObjectDatabaseTx();

        Transaction transaction = objectDatabaseTx.newInstance(Transaction.class, 1L);

        transactionDao.save(transaction);

        //        if(objectDatabaseTx.isActiveOnCurrentThread())
        //        {
        //            objectDatabaseTx.close();
        //        }

        objectDatabaseTx = repositoryConnection.getObjectDatabaseTx();

        transaction = objectDatabaseTx.newInstance(Transaction.class, 1L);

        transactionDao.save(transaction);

        //        if(objectDatabaseTx.isActiveOnCurrentThread())
        //        {
        //            objectDatabaseTx.close();
        //        }
    }

    @Test
    public void testFind()
    {
        OObjectDatabaseTx objectDatabaseTx = repositoryConnection.getObjectDatabaseTx();

        Transaction transaction = objectDatabaseTx.newInstance(Transaction.class, IdentifierUtils.nextLong());

        transactionDao.save(transaction);

        assertThat(transactionDao.find(transaction).getTid(), is(transaction.getTid()));

//        if (objectDatabaseTx.isActiveOnCurrentThread())
//        {
//            objectDatabaseTx.close();
//        }
    }

    @Test
    public void testFindList()
    {
        OObjectDatabaseTx objectDatabaseTx = repositoryConnection.getObjectDatabaseTx();

        Transaction transaction = objectDatabaseTx.newInstance(Transaction.class, IdentifierUtils.nextLong());

        transactionDao.save(transaction);

        assertThat(transactionDao.findList(), notNullValue());
        assertThat(transactionDao.findList().size(), greaterThanOrEqualTo(1));

//        if (objectDatabaseTx.isActiveOnCurrentThread())
//        {
//            objectDatabaseTx.close();
//        }
    }
}