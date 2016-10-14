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

import com.geeksaga.light.agent.RepositoryContext;
import com.geeksaga.light.agent.config.ConfigDefaultValueDef;
import com.geeksaga.light.agent.core.ActiveObject;
import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.geeksaga.light.repository.dao.TransactionDao;
import com.geeksaga.light.repository.dao.orientdb.TransactionDaoImpl;
import com.geeksaga.light.repository.entity.Transaction;
import com.geeksaga.light.repository.store.StoreFactory;
import com.geeksaga.light.repository.util.IdentifierUtils;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import java.util.concurrent.BlockingQueue;

import static com.geeksaga.light.agent.config.ConfigDef.db_url;

/**
 * @author geeksaga
 */
public class RepositoryWorker implements Runnable
{
    private final BlockingQueue<ActiveObject> queue;
    private final RepositoryContext repositoryContext;

    private final LightLogger logger;
    private final StoreFactory factory;

    public RepositoryWorker(BlockingQueue<ActiveObject> queue, RepositoryContext repositoryContext)
    {
        this.queue = queue;
        this.repositoryContext = repositoryContext;

        this.logger = CommonLogger.getLogger(getClass().getName());

        init();

        this.factory = StoreFactory.getInstance();
    }

    private void init()
    {
        System.setProperty("light.db.url", String.format("%s", System.getProperty("light.db.url", repositoryContext.getConfig().read(db_url, ConfigDefaultValueDef.default_db_url))));
    }

    @Override
    public void run()
    {
        logger.info(Thread.currentThread().getName() + " start");

        while (!Thread.currentThread().isInterrupted())
        {
            try
            {
                ActiveObject data = queue.take();

                save(data);
            }
            catch (InterruptedException e)
            {
                logger.warn(e);
            }
        }
    }

    public void save(final ActiveObject activeObject)
    {
        try
        {
            OObjectDatabaseTx objectDatabaseTx = factory.getObjectDatabaseTx();

            Transaction transaction = objectDatabaseTx.newInstance(Transaction.class, IdentifierUtils.nextLong());
            transaction.setTransactionName(activeObject.getTransactionName());
            transaction.setEndTimeMillis(System.currentTimeMillis());
            transaction.setElapsedTime((int) (transaction.getEndTimeMillis() - activeObject.getStartTimeMillis()));

            TransactionDao transactionDao = new TransactionDaoImpl(factory);
            transactionDao.save(transaction);

            logger.info("application = {}, end time = {}, elapsed time = {}", activeObject.getTransactionName(), transaction.getEndTimeMillis(), transaction.getElapsedTime());

            logger.info("find =========================================>");
            for (Transaction t : transactionDao.findList())
            {
                logger.info("{}", t.toString());
            }
        }
        catch (Exception exception)
        {
            logger.info(exception);
        }
    }
}
