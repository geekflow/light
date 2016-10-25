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

import com.geeksaga.light.repository.entity.Transaction;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

/**
 * @author geeksaga
 */
public class RepositoryConnection
{
    private OObjectDatabaseTx objectDatabaseTx = null;

    public RepositoryConnection(String database)
    {}

    public RepositoryConnection(String url, String database)
    {
        initObjectDatabaseTx(url, database);
    }

    public RepositoryConnection(ODatabaseDocumentTx databaseDocumentTx)
    {
        objectDatabaseTx = new OObjectDatabaseTx(databaseDocumentTx);
    }

    public OObjectDatabaseTx getObjectDatabaseTx()
    {
        if (!objectDatabaseTx.isActiveOnCurrentThread())
        {
            objectDatabaseTx.activateOnCurrentThread();
        }

        return objectDatabaseTx;
    }

    private OObjectDatabaseTx getDatabase(String url, String database, String user, String password)
    {
        OObjectDatabaseTx objectDatabaseTx = new OObjectDatabaseTx(url + database);
        if (!objectDatabaseTx.exists())
        {
            objectDatabaseTx.create();

            return objectDatabaseTx;
        }

        return objectDatabaseTx.open(user, password);
    }

    private void initObjectDatabaseTx(String url, String database)
    {
        objectDatabaseTx = getDatabase(url, database, "admin", "admin");

        objectDatabaseTx.setAutomaticSchemaGeneration(true);

        objectDatabaseTx.getEntityManager().registerEntityClasses(Transaction.class.getPackage().getName(), RepositoryConnection.class.getClassLoader());

        objectDatabaseTx.getMetadata().getSchema().synchronizeSchema();
    }

    public void close()
    {
        if (objectDatabaseTx != null && !objectDatabaseTx.isClosed())
        {
            objectDatabaseTx.close();
        }
    }

    public OClass findClass(String className)
    {
        OSchema schema = getObjectDatabaseTx().getMetadata().getSchema();

        getObjectDatabaseTx().close();

        return schema.getClass(className);
    }
}