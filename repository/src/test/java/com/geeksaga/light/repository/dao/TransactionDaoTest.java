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

import com.geeksaga.light.agent.config.ConfigDefaultValueDef;
import com.geeksaga.light.config.Config;
import com.geeksaga.light.profiler.config.ProfilerConfiguration;
import com.geeksaga.light.repository.Product;
import com.geeksaga.light.repository.dao.orientdb.TransactionDaoImpl;
import com.geeksaga.light.repository.entity.Transaction;
import com.geeksaga.light.repository.store.StoreFactory;
import com.geeksaga.light.repository.util.IdentifierUtils;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static com.geeksaga.light.agent.config.ConfigDef.db_url;
import static com.geeksaga.light.agent.config.ConfigDef.instance_id;
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
        System.setProperty("light.config", System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "light.conf");
        System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "log4j2.xml");

        System.setProperty("light.db.url", String.format("memory:/%s/", Product.NAME.toLowerCase()));

        Config config = ProfilerConfiguration.load();

        System.setProperty("light.db.url", String.format("%s", System.getProperty("light.db.url", config.read(db_url, ConfigDefaultValueDef.default_db_url))));

        factory = StoreFactory.getInstance(Product.NAME + "/" + config.read(instance_id, ConfigDefaultValueDef.default_instance_id));
        transactionDao = new TransactionDaoImpl(factory);
    }

    @Test
    public void testSave()
    {
        Transaction transaction = factory.getObjectDatabaseTx().newInstance(Transaction.class, IdentifierUtils.nextLong());

        transactionDao.save(transaction);
    }

    @Test(expected = com.orientechnologies.orient.core.storage.ORecordDuplicatedException.class)
    public void testUniqueIndex()
    {
        Transaction transaction = factory.getObjectDatabaseTx().newInstance(Transaction.class, 1L);

        transactionDao.save(transaction);

        transaction = factory.getObjectDatabaseTx().newInstance(Transaction.class, 1L);

        transactionDao.save(transaction);
    }

    @Test
    public void testFind()
    {
        Transaction transaction = factory.getObjectDatabaseTx().newInstance(Transaction.class, IdentifierUtils.nextLong());

        transactionDao.save(transaction);

        assertThat(transactionDao.find(transaction).getTid(), is(transaction.getTid()));
    }

    @Test
    public void testFindList()
    {
        Transaction transaction = factory.getObjectDatabaseTx().newInstance(Transaction.class, IdentifierUtils.nextLong());

        transactionDao.save(transaction);

        assertThat(transactionDao.findList(), notNullValue());
        assertThat(transactionDao.findList().size(), greaterThanOrEqualTo(1));
    }
}
