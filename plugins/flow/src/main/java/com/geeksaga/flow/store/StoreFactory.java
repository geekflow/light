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
package com.geeksaga.flow.store;

import com.geeksaga.light.util.SystemProperty;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import java.util.List;

/**
 * @author geeksaga
 */
public class StoreFactory
{
    private static StoreFactory instance = null;
    private static OrientGraphFactory factory = null;

    private static String path = System.getProperty("flow.db.path", "plocal:./databases/");

    private StoreFactory() {}

    public static StoreFactory getInstance()
    {
        return getInstance("flow");
    }

    public static StoreFactory getInstance(String database)
    {
        if (instance == null)
        {
            instance = new StoreFactory();

            init(database);

            factory = new OrientGraphFactory(path + database);

            initTableClass();

            if (factory.exists())
            {
                factory.setupPool(1, 10);
            }
        }

        return instance;
    }

    public ODatabaseDocumentTx getDatabase()
    {
        return factory.getDatabase();
    }

    private static void init(String database)
    {
        ODatabaseDocumentTx documentTx = new ODatabaseDocumentTx(path + database);

        if (!documentTx.exists())
        {
            documentTx.create();
            //database.open(OrientBaseGraph.ADMIN, OrientBaseGraph.ADMIN);
        }
        //        else
        //        {
        //            database.create();
        //        }
    }

    private static void initTableClass()
    {
        ODatabaseDocumentTx documentTx = factory.getDatabase();
        OSchema schema = documentTx.getMetadata().getSchema();
        OClass oClass = schema.getClass("Classes");

        if (oClass == null)
        {
            oClass = schema.createClass("Classes");
            oClass.createProperty("name", OType.STRING);
            oClass.createProperty("byteCode", OType.BINARY);

            oClass.createIndex("ClassNameUnique", OClass.INDEX_TYPE.UNIQUE, "name");
        }
    }

    public boolean store(String className, String fieldName, String propertyValue)
    {
        ODatabaseDocumentTx documentTx = factory.getDatabase();
        System.out.println(documentTx.getName()); // flow

        try
        {
            documentTx.begin();

            List<ODocument> result = documentTx.query(new OSQLSynchQuery<ODocument>("SELECT * FROM Classes where name = '" + propertyValue + "'"));

            System.out.println("selected size : " + result.size() + ", " + propertyValue);

            for (ODocument document : result)
            {
                System.out.println(document);
            }

            //            if(documentTx.query(new OSQLSynchQuery<ODocument>("SELECT * FROM " + className + " where ame = 'Luke' and name like 'L%'")))
            //            documentTx.getMetadata().getSchema().getClass();

            if (result.size() == 0)
            {
                ODocument document = new ODocument("Classes");
                document.field(fieldName, propertyValue);
                document.save();
            }
            else
            {
                return true;
                //                for(ODocument document : documentTx.browseClass("Classes"))
                //                {
                //                    document.field(fieldName, propertyValue);
                //                    document.save();
                //                }
            }

            documentTx.commit();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();

            documentTx.rollback();

            return false;
        }
        finally
        {
            documentTx.close();
        }

        return true;
    }
}
