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
package com.geeksaga.light.repository.dao.orientdb;

import com.geeksaga.light.repository.dao.TransactionDao;
import com.geeksaga.light.repository.entity.Transaction;
import com.geeksaga.light.repository.factory.RepositoryFactory;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import java.util.List;

/**
 * @author geeksaga
 */
public class TransactionDaoImpl implements TransactionDao
{
    private RepositoryFactory repositoryFactory;

    public TransactionDaoImpl(RepositoryFactory repositoryFactory)
    {
        this.repositoryFactory = repositoryFactory;
    }

    @Override
    public boolean save(Transaction transaction)
    {
        OObjectDatabaseTx documentTx = repositoryFactory.getObjectDatabaseTx();
        documentTx.save(transaction);

        return true;
    }

    @Override
    public Transaction modify(Transaction transaction)
    {
        OObjectDatabaseTx documentTx = repositoryFactory.getObjectDatabaseTx();

        try
        {
            documentTx.begin();

            //            List<ODocument> result = documentTx.query(new OSQLSynchQuery<ODocument>("SELECT * FROM transaction WHERE id = " + transaction.getId() + ""));
            List<ODocument> result = documentTx.query(new OSQLSynchQuery<ODocument>("SELECT * FROM transaction"));


            //                int recordsUpdated = documentTx.command(new OCommandSQL("UPDATE Classes SET byteCode = " + classes.getByteCodes() + " WHERE name = " + classes.getName())).execute();

            //                for (ODocument document : documentTx.browseClass("transaction"))
            //                {
            //                    document.field("id", transaction.getId());
            //                    document.field("oid", transaction.getOid());
            //                    document.field("guid", transaction.getGuid());
            //                    document.field("endTime", transaction.getEndTime());
            //                    document.field("elapsedTime", transaction.getElapsedTime());
            //                    document.field("cpuTime", transaction.getCpuTime());
            //                    document.field("sqlCount", transaction.getSqlCount());
            //                    document.field("sqlTime", transaction.getSqlTime());
            //                    document.field("fetchCount", transaction.getFetchCount());
            //                    document.field("fetchTime", transaction.getFetchTime());
            //                    document.field("ipAddress", transaction.getIpAddress());
            //                    document.field("transactionHash", transaction.getTransactionHash());
            //                    document.field("browserHash", transaction.getBrowserHash());
            //                    document.field("userHash", transaction.getUserHash());
            //                    document.save();
            //                }
            //                documentTx.getMetadata().getSchema().reload();

            documentTx.commit();
        }
        catch (Exception exception)
        {
            documentTx.rollback();
        }
        finally
        {
            documentTx.close();
        }

        return transaction;
    }

    @Override
    public Transaction find(Transaction transaction)
    {
        OObjectDatabaseTx documentTx = repositoryFactory.getObjectDatabaseTx();

        List<Transaction> result = documentTx.command(new OSQLSynchQuery<Transaction>("SELECT * FROM Transaction WHERE tid = " + transaction.getTid())).execute();

        for (Transaction storedTransaction : result)
        {
            return storedTransaction;
        }

        return new Transaction();
    }

    @Override
    public List<Transaction> findList()
    {
        OObjectDatabaseTx documentTx = repositoryFactory.getObjectDatabaseTx();

        return documentTx.query(new OSQLSynchQuery<Transaction>("SELECT * FROM Transaction"));
    }
}
