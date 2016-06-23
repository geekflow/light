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
package com.geeksaga.light.repository.store;

import com.geeksaga.light.repository.Product;
import com.geeksaga.light.repository.entity.Transaction;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

/**
 * @author geeksaga
 */
public class StoreFactory
{
    private static StoreFactory instance = null;
    private static OObjectDatabaseTx factory = null;

    private static String path = System.getProperty("light.db.path", "plocal:./databases/");

    private StoreFactory()
    {
        this(Product.NAME);
    }

    private StoreFactory(String database)
    {
        init(database);

        factory = new OObjectDatabaseTx(path + database);

        if (factory.exists())
        {
            factory = new OObjectDatabaseTx(path + database).open("admin", "admin");
            //            factory.setupPool(1, 10);
        }
        else
        {
            factory.create();
        }

        //        factory.getMetadata().getSchema().generateSchema(Transaction.class);
        factory.setAutomaticSchemaGeneration(true);

        factory.getEntityManager().registerEntityClasses(Transaction.class.getPackage().getName());

        initTableClass();
    }

    public static StoreFactory getInstance()
    {
        return getInstance(Product.NAME);
    }

    public static StoreFactory getInstance(String database)
    {
        if (instance == null)
        {
            instance = new StoreFactory(database);
        }

        return instance;
    }

    public OObjectDatabaseTx getDatabase()
    {
        return factory;
    }

    private static void init(String database)
    {
        ODatabaseDocumentTx documentTx = new ODatabaseDocumentTx(path + database);

        if (!documentTx.exists())
        {
            documentTx.create();
        }
    }

    private static void initTableClass()
    {
        //        ODatabaseDocumentTx documentTx = factory.getDatabase();
        OObjectDatabaseTx documentTx = factory;
        OClass oClass = documentTx.getMetadata().getSchema().getClass(Transaction.class);

        if (oClass != null)
        {
            oClass.createIndex("TransactionIdUnique", OClass.INDEX_TYPE.UNIQUE, "tid");
        }
    }

    public static OClass findClass(String className)
    {
        //        ODatabaseDocumentTx documentTx = factory.getDatabase();
        OObjectDatabaseTx documentTx = factory;
        OSchema schema = documentTx.getMetadata().getSchema();

        return schema.getClass(className);
    }
}
