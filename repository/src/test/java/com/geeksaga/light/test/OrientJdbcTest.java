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
package com.geeksaga.light.test;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.jdbc.OrientJdbcConnection;
import com.orientechnologies.orient.jdbc.OrientJdbcDriver;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import static java.lang.Class.forName;

/**
 * @author geeksaga
 */
public class OrientJdbcTest
{
    private OrientJdbcConnection orientJdbcConnection;

    @Ignore
    @Test
    public void testJdbc() throws Exception
    {
        //load Driver
        forName(OrientJdbcDriver.class.getName());
        String dbUrl = "memory:testdb";
        ODatabaseDocumentTx db = new ODatabaseDocumentTx(dbUrl);
        createSchemaDB(db);
        loadDB(db, 20);
        db.create();

        Properties info = new Properties();
        info.put("user", "admin");
        info.put("password", "admin");
        orientJdbcConnection = (OrientJdbcConnection) DriverManager.getConnection("jdbc:orient:" + dbUrl, info);

        //create and execute statement
        Statement stmt = orientJdbcConnection.createStatement();
        int updated = stmt.executeUpdate("INSERT into emplyoee (key, text) values('001', 'satish')");

        if (orientJdbcConnection != null && !orientJdbcConnection.isClosed())
        {
            orientJdbcConnection.close();
        }
    }

    private void createSchemaDB(ODatabaseDocumentTx db)
    {
        OSchema schema = db.getMetadata().getSchema();

        // item
        OClass item = schema.createClass("Item");

        item.createProperty("stringKey", OType.STRING).createIndex(OClass.INDEX_TYPE.UNIQUE);
        item.createProperty("intKey", OType.INTEGER).createIndex(OClass.INDEX_TYPE.UNIQUE);
        item.createProperty("date", OType.DATE).createIndex(OClass.INDEX_TYPE.NOTUNIQUE);
        item.createProperty("time", OType.DATETIME).createIndex(OClass.INDEX_TYPE.NOTUNIQUE);
        item.createProperty("text", OType.STRING);
        item.createProperty("length", OType.LONG).createIndex(OClass.INDEX_TYPE.NOTUNIQUE);
        item.createProperty("published", OType.BOOLEAN).createIndex(OClass.INDEX_TYPE.NOTUNIQUE);
        item.createProperty("title", OType.STRING).createIndex(OClass.INDEX_TYPE.NOTUNIQUE);
        item.createProperty("author", OType.STRING).createIndex(OClass.INDEX_TYPE.NOTUNIQUE);
        item.createProperty("tags", OType.EMBEDDEDLIST);

    }

    private void loadDB(ODatabaseDocumentTx db, int documents) throws IOException
    {
        db.declareIntent(new OIntentMassiveInsert());

        for (int i = 1; i <= documents; i++)
        {
            ODocument doc = new ODocument();
            doc.setClassName("Item");
            doc = createItem(i, doc);
            db.save(doc, "Item");

        }

        db.declareIntent(null);
    }

    private ODocument createItem(int id, ODocument doc)
    {
        String itemKey = Integer.valueOf(id).toString();

        doc.setClassName("Item");
        doc.field("stringKey", itemKey);
        doc.field("intKey", id);
        String contents = "OrientDB is a deeply scalable Document-Graph DBMS with the flexibility of the Document databases and the power to manage links of the Graph databases. " + "It can work in schema-less mode, schema-full or a mix of both. Supports advanced features such as ACID Transactions, Fast Indexes, Native and SQL queries." + " It imports and exports documents in JSON." + " Graphs of hundreads of linked documents can be retrieved all in memory in few milliseconds without executing costly JOIN such as the Relational DBMSs do. " + "OrientDB uses a new indexing algorithm called MVRB-Tree, derived from the Red-Black Tree and from the B+Tree with benefits of both: fast insertion and ultra fast lookup. " + "The transactional engine can run in distributed systems supporting up to 9.223.372.036 Billions of records for the maximum capacity of 19.807.040.628.566.084 Terabytes of data distributed on multiple disks in multiple nodes. " + "OrientDB is FREE for any use. Open Source License Apache 2.0. ";
        doc.field("text", contents);
        doc.field("title", "orientDB");
        doc.field("length", contents.length());
        doc.field("published", (id % 2 > 0));
        doc.field("author", "anAuthor" + id);
        // doc.field("tags", asList("java", "orient", "nosql"), OType.EMBEDDEDLIST);
        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        instance.add(Calendar.HOUR_OF_DAY, -id);
        Date time = instance.getTime();
        doc.field("date", time, OType.DATE);
        doc.field("time", time, OType.DATETIME);

        return doc;
    }
}
