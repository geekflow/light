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
import com.geeksaga.light.repository.connect.RepositorySource;
import com.geeksaga.light.repository.dao.TransactionDao;
import com.geeksaga.light.repository.dao.orientdb.TransactionDaoImpl;
import com.geeksaga.light.repository.entity.Transaction;
import com.geeksaga.light.repository.util.IdentifierUtils;

import java.util.concurrent.BlockingQueue;

/**
 * @author geeksaga
 */
public class TraceRepositoryWorker implements Runnable
{
    private final LightLogger logger;
    private final TraceRepository traceRepository;
    private final RepositorySource repositorySource;
    private final BlockingQueue<ActiveObject> queue;

    private TransactionDao transactionDao;

    public TraceRepositoryWorker(TraceRepository traceRepository, BlockingQueue<ActiveObject> queue)
    {
        this(traceRepository, new RepositorySource(Product.NAME.toLowerCase()), queue);
    }

    public TraceRepositoryWorker(TraceRepository traceRepository, RepositorySource repositorySource, BlockingQueue<ActiveObject> queue)
    {
        this.logger = CommonLogger.getLogger(getClass().getName());

        this.traceRepository = traceRepository;
        this.repositorySource = repositorySource;

        this.queue = queue;

        init();
    }

    private void init()
    {
        this.transactionDao = new TransactionDaoImpl(repositorySource);
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
            Transaction transaction = createTransaction(activeObject);

            transactionDao.save(transaction);

            logger.info("application = {}, end time = {}, elapsed time = {}", transaction.getTransactionName(), transaction.getEndTime(), transaction.getElapsedTime());
        }
        catch (Exception exception)
        {
            logger.info(exception);
        }
    }

    private Transaction createTransaction(final ActiveObject activeObject)
    {
//        Transaction transaction = repositorySource.getObjectDatabaseTx().newInstance(Transaction.class, IdentifierUtils.nextLong());
        Transaction transaction = new Transaction(IdentifierUtils.nextLong());
        transaction.setTransactionName(activeObject.getTransactionName());
        transaction.setEndTime(System.currentTimeMillis());
        transaction.setElapsedTime((int) (System.nanoTime() - activeObject.getStartTimeMillis()));

        return transaction;
    }
}