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
package com.geeksaga.light.repository.connect;

import com.geeksaga.light.repository.Product;
import com.geeksaga.light.repository.entity.Transaction;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.query.OQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import java.util.ArrayList;
import java.util.List;

/**
 * @author geeksaga
 */
public class RepositoryExecutor
{
    private RepositorySource repositorySource;

    public RepositoryExecutor()
    {
        this(new RepositorySource(Product.NAME.toLowerCase()));
    }

    public RepositoryExecutor(RepositorySource repositorySource)
    {
        this.repositorySource = repositorySource;
    }

    public void execute()
    {

    }

    public <RET> RET save(final Object iContent)
    {
        final OObjectDatabaseTx databaseTx = repositorySource.getObjectDatabaseTx();

        try
        {
            return (RET) databaseTx.save(iContent);
        }
        finally
        {
            databaseTx.close();
        }
    }

    public <RET extends OCommandRequest> RET command(final OCommandRequest iCommand)
    {
        final OObjectDatabaseTx databaseTx = repositorySource.getObjectDatabaseTx();

        try
        {
            return (RET) databaseTx.command(iCommand);
        }
        finally
        {
            databaseTx.close();
        }
    }

    public <RET extends List<?>> RET query(OQuery<?> iCommand, Object... iArgs)
    {
        final OObjectDatabaseTx databaseTx = repositorySource.getObjectDatabaseTx();

        List<Transaction> list = databaseTx.query(iCommand, iArgs);

        try
        {
            return (RET) list;
            // FIXME jacoco build error
            //            return (RET) detach(databaseTx, list);
        }
        finally
        {
            databaseTx.close();
        }
    }

    @SuppressWarnings("unchecked")
    public <RET extends List<?>> RET detach(final OObjectDatabaseTx databaseTx, RET entities)
    {
        List<Object> result = new ArrayList<Object>(entities.size());
        for (Object entity : entities)
        {
            result.add(databaseTx.detach(entity, true));
        }

        return (RET) result;
    }

    @SuppressWarnings("unchecked")
    public <RET extends List<?>> RET detachAll(final OObjectDatabaseTx databaseTx, RET entities)
    {
        List<Object> result = new ArrayList<Object>(entities.size());
        for (Object entity : entities)
        {
            result.add(databaseTx.detachAll(entity, true));
        }

        return (RET) result;
    }

    @SuppressWarnings("unchecked")
    public <RET> RET detach(final OObjectDatabaseTx databaseTx, RET entity)
    {
        return (RET) databaseTx.detach(entity, true);
    }

    @SuppressWarnings("unchecked")
    public <RET> RET detachAll(final OObjectDatabaseTx databaseTx, RET entity)
    {
        return (RET) databaseTx.detachAll(entity, true);
    }
}
