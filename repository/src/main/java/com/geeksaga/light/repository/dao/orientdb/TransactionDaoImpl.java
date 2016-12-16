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

import com.geeksaga.light.repository.connect.RepositorySource;
import com.geeksaga.light.repository.dao.TransactionDao;
import com.geeksaga.light.repository.entity.Transaction;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import java.util.ArrayList;
import java.util.List;

/**
 * @author geeksaga
 */
public class TransactionDaoImpl implements TransactionDao
{
    private RepositorySource repositorySource;

    public TransactionDaoImpl(RepositorySource repositorySource)
    {
        this.repositorySource = repositorySource;
    }

    public OObjectDatabaseTx getObjectDatabase()
    {
        return repositorySource.openDatabase();
    }

    @Override
    public boolean save(Transaction transaction)
    {
        OObjectDatabaseTx databaseTx = repositorySource.getObjectDatabaseTx();

        try
        {
            databaseTx.save(transaction);
        }
        finally
        {
            databaseTx.close();
        }

        return true;
    }

    @Override
    public Transaction modify(Transaction transaction)
    {
        OObjectDatabaseTx databaseTx = repositorySource.getObjectDatabaseTx();

        try
        {
            databaseTx.begin();

            //            List<ODocument> result = documentTx.query(new OSQLSynchQuery<ODocument>("SELECT * FROM transaction WHERE id = " + transaction.getId() + ""));
            List<ODocument> result = databaseTx.query(new OSQLSynchQuery<ODocument>("SELECT * FROM transaction"));


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

            databaseTx.commit();
        }
        catch (Exception exception)
        {
            databaseTx.rollback();
        }
        finally
        {
            databaseTx.close();
        }

        return transaction;
    }

    @Override
    public Transaction find(Transaction transaction)
    {
        OObjectDatabaseTx objectDatabaseTx = repositorySource.getObjectDatabaseTx();

        List<Transaction> result = objectDatabaseTx.command(new OSQLSynchQuery<Transaction>("SELECT * FROM Transaction WHERE tid = " + transaction.getTid())).execute();

        objectDatabaseTx.close();

        for (Transaction storedTransaction : result)
        {
            return storedTransaction;
        }

        return new Transaction();
    }

    @Override
    public List<Transaction> findList()
    {
        final OObjectDatabaseTx databaseTx = repositorySource.getObjectDatabaseTx();

        List<Transaction> list = databaseTx.query(new OSQLSynchQuery<Transaction>("SELECT * FROM Transaction ORDER BY endTime DESC"));

//        List<Object> result = new ArrayList<Object>(list.size());
//        for (Object entity : list)
//        {
//            result.add(databaseTx.detach(entity, true));
//        }

        databaseTx.close();

        return list;
    }

    @SuppressWarnings("unchecked")
    public <RET extends List<?>> RET detach(RET entities)
    {
        final OObjectDatabaseTx db = getObjectDatabase();

        List<Object> result = new ArrayList<Object>(entities.size());
        for (Object entity : entities)
        {
            result.add(db.detach(entity, true));
        }

        return (RET) result;
    }

    @SuppressWarnings("unchecked")
    public <RET extends List<?>> RET detachAll(RET entities)
    {
        final OObjectDatabaseTx db = getObjectDatabase();

        List<Object> result = new ArrayList<Object>(entities.size());
        for (Object entity : entities)
        {
            result.add(db.detachAll(entity, true));
        }

        return (RET) result;
    }

    @SuppressWarnings("unchecked")
    public <RET> RET detach(RET entity)
    {
        return (RET) getObjectDatabase().detach(entity, true);
    }

    @SuppressWarnings("unchecked")
    public <RET> RET detachAll(RET entity)
    {
        return (RET) getObjectDatabase().detachAll(entity, true);
    }
}
