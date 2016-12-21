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

import com.geeksaga.light.agent.profile.ProfileMethod;
import com.geeksaga.light.repository.connect.RepositoryExecutor;
import com.geeksaga.light.repository.connect.RepositorySource;
import com.geeksaga.light.repository.dao.TransactionProfileDao;
import com.geeksaga.light.repository.entity.ProfileData;
import com.geeksaga.light.repository.entity.Transaction;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import java.util.List;

/**
 * @author geeksaga
 */
public class TransactionProfileDaoImpl implements TransactionProfileDao
{
    private RepositoryExecutor repositoryExecutor;

    public TransactionProfileDaoImpl(RepositoryExecutor repositoryExecutor)
    {
        this.repositoryExecutor = repositoryExecutor;
    }

    @Override
    public ProfileData save(ProfileData profileData)
    {
        return repositoryExecutor.save(profileData);
    }

    @Override
    public ProfileData modify(ProfileData profileData)
    {
//        OObjectDatabaseTx databaseTx = repositorySource.getObjectDatabaseTx();
//
//        try
//        {
//            databaseTx.begin();
//
//            //            List<ODocument> result = documentTx.query(new OSQLSynchQuery<ODocument>("SELECT * FROM transaction WHERE id = " + transaction.getId() + ""));
//            List<ODocument> result = databaseTx.query(new OSQLSynchQuery<ODocument>("SELECT * FROM transaction"));
//
//            databaseTx.commit();
//        }
//        catch (Exception exception)
//        {
//            databaseTx.rollback();
//        }
//        finally
//        {
//            databaseTx.close();
//        }

        return profileData;
    }

    @Override
    public ProfileData find(Transaction transaction)
    {
        List<ProfileData> result = repositoryExecutor.command(new OSQLSynchQuery<ProfileData>("SELECT * FROM Transaction WHERE tid = " + transaction.getTid())).execute();
        if (result.size() > 0)
        {
            return result.get(0);
        }

        return new ProfileData();
    }

    @Override
    public List<ProfileData> findList()
    {
        return repositoryExecutor.query(new OSQLSynchQuery<ProfileData>("SELECT * FROM Transaction ORDER BY endTime DESC"));
    }
}
