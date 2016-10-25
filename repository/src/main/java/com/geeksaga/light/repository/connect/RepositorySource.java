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

import com.geeksaga.light.config.Config;
import com.geeksaga.light.repository.entity.Transaction;
import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import static com.geeksaga.light.agent.config.ConfigDefaultValueDef.default_db_url;

/**
 * @author geeksaga
 */
public class RepositorySource
{
    private OPartitionedDatabasePool partitionedDatabasePool;
    private OObjectDatabaseTx objectDatabaseTx = null;
    private Config config;
    private String database;

    private static String dbUrl = System.getProperty("light.db.url", default_db_url);

    public RepositorySource(String database)
    {
        this(null, database);
    }

    public RepositorySource(Config config, String database)
    {
        this.config = config;
        this.database = database;

        initObjectDatabaseTx(database);

        initObjectDatabaseSchema();

        initTableClass();
    }

    public RepositoryConnection getConnection()
    {
        return new RepositoryConnection(partitionedDatabasePool.acquire());
    }

    public OObjectDatabaseTx getObjectDatabaseTx()
    {
        OObjectDatabaseTx objectDatabaseTx = new OObjectDatabaseTx(partitionedDatabasePool.acquire());
        if (!objectDatabaseTx.isActiveOnCurrentThread())
        {
            objectDatabaseTx.activateOnCurrentThread();
        }

        return objectDatabaseTx;
    }

    public OPartitionedDatabasePool getPartitionedDatabasePool()
    {
        return partitionedDatabasePool;
    }

    private void initDatabaseDocumentTx(String database)
    {
        ODatabaseDocumentTx documentTx = new ODatabaseDocumentTx(dbUrl + database);
        if (!documentTx.exists())
        {
            documentTx.create();
        }
    }

    private void initObjectDatabaseTx(String database)
    {
        //        OPartitionedDatabasePoolFactory poolFactory = new OPartitionedDatabasePoolFactory(30);
        //        OPartitionedDatabasePool pool = poolFactory.get(dbUrl,  "admin", "admin");

        partitionedDatabasePool = new OPartitionedDatabasePool(dbUrl, "admin", "admin", 32, 10);
        partitionedDatabasePool.setAutoCreate(true);

        //        OServerAdmin serverAdmin = new OServerAdmin(dbUrl).connect("root", "root");
        //        serverAdmin.createDatabase(database, "object", "plocal");
    }

    private void initObjectDatabaseSchema()
    {
        objectDatabaseTx = new OObjectDatabaseTx(getPartitionedDatabasePool().acquire());
//        if (objectDatabaseTx.exists())
//        {
//            objectDatabaseTx.open("admin", "admin");
//        }
//        else
//        {
//            objectDatabaseTx.create();
//        }

        objectDatabaseTx.setAutomaticSchemaGeneration(true);

        objectDatabaseTx.getEntityManager().registerEntityClasses(Transaction.class.getPackage().getName(), RepositorySource.class.getClassLoader());

        objectDatabaseTx.getMetadata().getSchema().synchronizeSchema();

        objectDatabaseTx.close();
    }

    private void initTableClass()
    {
        OObjectDatabaseTx objectDatabaseTx = getObjectDatabaseTx();

        OClass oClass = objectDatabaseTx.getMetadata().getSchema().getClass(Transaction.class);

        if (oClass != null && !oClass.areIndexed("tid"))
        {
            oClass.createIndex("TransactionIdUnique", OClass.INDEX_TYPE.UNIQUE, "tid");
        }

        objectDatabaseTx.close();
    }

    public OClass findClass(String className)
    {
        //        ODatabaseDocumentTx documentTx = objectDatabaseTx.getDatabase();
        OSchema schema = getObjectDatabaseTx().getMetadata().getSchema();

        return schema.getClass(className);
    }
}