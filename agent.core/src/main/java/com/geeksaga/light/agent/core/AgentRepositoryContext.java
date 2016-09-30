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
package com.geeksaga.light.agent.core;

import com.geeksaga.light.agent.RepositoryContext;
import com.geeksaga.light.config.Config;
import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.geeksaga.light.repository.Product;
import com.geeksaga.light.repository.dao.TransactionDao;
import com.geeksaga.light.repository.dao.orientdb.TransactionDaoImpl;
import com.geeksaga.light.repository.entity.Transaction;
import com.geeksaga.light.repository.store.StoreFactory;

/**
 * @author geeksaga
 */
public class AgentRepositoryContext implements RepositoryContext
{
    private LightLogger logger;
    private Config config;

    public AgentRepositoryContext(Config config)
    {
        this.logger = CommonLogger.getLogger(getClass().getName());
        this.config = config;
    }

    public void save(ActiveObject activeObject)
    {
        System.setProperty("light.db.path", String.format("memory:/%s/", Product.NAME.toUpperCase()));

        Transaction transaction = new Transaction(activeObject.hashCode());
        transaction.setEndTime(System.currentTimeMillis());
        transaction.setElapsedTime((int) (transaction.getEndTime() - activeObject.getStartTime()));

        TransactionDao transactionDao = new TransactionDaoImpl(StoreFactory.getInstance(Product.NAME));
        transactionDao.save(transaction);

        for (Transaction t : transactionDao.findList())
        {
            logger.info("application = {}, end time = {}, elapsed time = {}", "TestURL", transaction.getEndTime(), transaction.getElapsedTime());
        }
    }

    public Config getConfig()
    {
        return config;
    }
}
