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

import com.geeksaga.light.agent.TraceRepository;
import com.geeksaga.light.agent.core.ActiveObject;
import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.geeksaga.light.repository.dao.TransactionDao;
import com.geeksaga.light.repository.dao.orientdb.TransactionDaoImpl;
import com.geeksaga.light.repository.entity.Transaction;
import com.geeksaga.light.repository.store.RepositoryFactory;
import com.geeksaga.light.repository.util.IdentifierUtils;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import java.util.concurrent.BlockingQueue;

/**
 * @author geeksaga
 */
public class TraceRepositoryWorker implements Runnable
{
    private final LightLogger logger;
    private final TraceRepository traceRepository;
    private final RepositoryFactory repositoryFactory;
    private final BlockingQueue<ActiveObject> queue;

    public TraceRepositoryWorker(TraceRepository traceRepository, BlockingQueue<ActiveObject> queue)
    {
        this(traceRepository, RepositoryFactory.getInstance(), queue);
    }

    public TraceRepositoryWorker(TraceRepository traceRepository, RepositoryFactory repositoryFactory, BlockingQueue<ActiveObject> queue)
    {
        this.logger = CommonLogger.getLogger(getClass().getName());

        this.traceRepository = traceRepository;
        this.repositoryFactory = repositoryFactory;

        this.queue = queue;
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
            OObjectDatabaseTx objectDatabaseTx = repositoryFactory.getObjectDatabaseTx();

            Transaction transaction = objectDatabaseTx.newInstance(Transaction.class, IdentifierUtils.nextLong());
            transaction.setTransactionName(activeObject.getTransactionName());
            transaction.setEndTimeMillis(System.currentTimeMillis());
            transaction.setElapsedTime((int) (transaction.getEndTimeMillis() - activeObject.getStartTimeMillis()));

            TransactionDao transactionDao = new TransactionDaoImpl(repositoryFactory);
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
