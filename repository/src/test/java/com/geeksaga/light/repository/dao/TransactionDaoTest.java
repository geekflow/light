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

import com.geeksaga.light.repository.Product;
import com.geeksaga.light.repository.dao.orientdb.TransactionDaoImpl;
import com.geeksaga.light.repository.entity.Transaction;
import com.geeksaga.light.repository.store.StoreFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author geeksaga
 */
public class TransactionDaoTest
{
    private static StoreFactory factory;
    private static TransactionDao transactionDao;

    @BeforeClass
    public static void init()
    {
        System.setProperty("light.db.path", String.format("memory:/%s/", Product.NAME.toUpperCase()));

        factory = StoreFactory.getInstance(Product.NAME);
        transactionDao = new TransactionDaoImpl(factory);
    }

    @Test
    public void testSave()
    {
        Transaction transaction = factory.getObjectDatabaseTx().newInstance(Transaction.class, 1L);

        transactionDao.save(transaction);
    }

    @Test(expected = com.orientechnologies.orient.core.storage.ORecordDuplicatedException.class)
    public void testUniqueIndex()
    {
        testSave();
        testSave();
    }

    @Test
    public void testFind()
    {
        Transaction transaction = factory.getObjectDatabaseTx().newInstance(Transaction.class, 2L);

        transactionDao.save(transaction);

        assertThat(transactionDao.find(transaction).getTid(), is(transaction.getTid()));
    }

    @Test
    public void testFindList()
    {
        Transaction transaction = factory.getObjectDatabaseTx().newInstance(Transaction.class, 3L);

        transactionDao.save(transaction);

        assertThat(transactionDao.findList(), notNullValue());
        assertThat(transactionDao.findList().size(), greaterThanOrEqualTo(1));
    }
}
