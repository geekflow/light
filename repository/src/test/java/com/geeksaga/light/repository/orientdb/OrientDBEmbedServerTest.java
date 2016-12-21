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
package com.geeksaga.light.repository.orientdb;

import com.geeksaga.light.repository.connect.RepositorySource;
import com.geeksaga.light.repository.dao.TransactionDao;
import com.geeksaga.light.repository.dao.orientdb.TransactionDaoImpl;
import com.geeksaga.light.repository.entity.Transaction;
import com.geeksaga.light.test.TestConfigure;
import com.geeksaga.light.util.IdentifierUtils;
import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

/**
 * @author geeksaga
 */
public class OrientDBEmbedServerTest
{
    private static final OrientDBEmbedServer server = new OrientDBEmbedServer();
    private static final String REPOSITORY_CONFIG = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "db.xml";

    @BeforeClass
    public static void init()
    {
        TestConfigure.load();

        System.setProperty("server.database.path", System.getProperty("user.dir") + File.separator + "databases");
        System.setProperty("light.repository.config", REPOSITORY_CONFIG);
        System.setProperty("light.db.url", "remote:localhost/");
    }

    @Ignore
    @Test
    public void testStartup()
    {
        server.startup(REPOSITORY_CONFIG);

        assertThat(server.isActive(), is(true));

        TestConfigure.load();

        RepositorySource repositorySource = TestConfigure.getRepositorySource();

        TransactionDao transactionDao = new TransactionDaoImpl(TestConfigure.getRepositoryExecutor());
        OPartitionedDatabasePool partitionedDatabasePool = repositorySource.getPartitionedDatabasePool();

        System.out.println(partitionedDatabasePool.getAvailableConnections() + " = " + partitionedDatabasePool.getCreatedInstances());

        OObjectDatabaseTx objectDatabaseTx = repositorySource.getObjectDatabaseTx();

        Transaction transaction = objectDatabaseTx.newInstance(Transaction.class, IdentifierUtils.nextLong());

        objectDatabaseTx.close();

        transactionDao.save(transaction);

        System.out.println(transactionDao.findList());
        System.out.println(partitionedDatabasePool.getAvailableConnections() + " = " + partitionedDatabasePool.getCreatedInstances());

        assertThat(server.shutdown(), is(true));
    }

    @Ignore
    @Test
    public void testShutdown()
    {
        assumeThat(server, notNullValue());
        assumeThat(server.isActive(), is(true));

        assertThat(server.shutdown(), is(true));
    }

    @Ignore
    @Test
    public void test()
    {

        //        ODatabaseDocumentTx db = new ODatabaseDocumentTx("local:petshop").open("admin", "admin");
        //
        //        // CREATE A NEW DOCUMENT AND FILL IT
        //        ODocument doc = new ODocument("Person");
        //        doc.field( "name", "Luke" );
        //        doc.field( "surname", "Skywalker" );
        //        doc.field( "city", new ODocument("City").field("name","Rome").field("country", "Italy") );
        //
        //        // SAVE THE DOCUMENT
        //        doc.save();
        //
        //        db.close();

        //        new OrientGraph("local:flow").create();

        //        ODatabaseDocumentTx db = new ODatabaseDocumentTx ("plocal:./databases/flowtemp").create();

        OrientGraphFactory factory = new OrientGraphFactory("plocal:./databases/flowtemp").setupPool(1, 10);

        ODatabaseDocumentTx db = factory.getDatabase();//new ODatabaseDocumentTx ("plocal:./databases/flowtemp").open("admin", "admin");

        //OClass c = db.getMetadata().getSchema().createClass("Person");
        OClass c = db.getMetadata().getSchema().getClass("Person2");
        //c.createIndex("PersonName", OClass.INDEX_TYPE.UNIQUE, "name");

        //        c.createProperty("name", OType.STRING);
        //        c.createProperty("surname", OType.STRING);
        //        c.createProperty("city", OType.LINK);

        System.out.println(c.count());
        ODocument doc = new ODocument(c);
        doc.field("name", "Luke");
        doc.field("surname", "Skywalker");
        doc.field("city", new ODocument("City").field("name", "Rome").field("country", "Italy"));
        doc.save();

        System.out.println(doc.getClassName());
        System.out.println(doc.getSize());
        System.out.println(doc.getSchemaClass());
        System.out.println(doc.getVersion());
        System.out.println(doc.getDatabase());

        System.out.println(db.getName());

        System.out.println(db.countClass("Person"));

        for (ODocument document : db.browseClass("Person"))
        {
            System.out.println(document);
            System.out.println(document.field("name"));
        }

        List<ODocument> result = db.query(new OSQLSynchQuery<ODocument>("select * from Person where name = 'Luke' and name like 'L%'"));

        for (ODocument document : result)
        {
            System.out.println(document);
        }

        db.close();


        OrientGraph graph = new OrientGraph("plocal:./databases/flowtemp");
        try
        {
            Vertex luca = graph.addVertex(null);
            luca.setProperty("name", "Luca");
            Vertex geek = graph.addVertex(null);
            geek.setProperty("name", "GeekSaga");
            graph.addEdge(null, luca, geek, "dev");
            graph.commit();

            System.out.println("===============");
            System.out.println(graph.query());//"CREATE CLASS geeksaga.flow.jar");

            for (Vertex v : graph.query().has("name").vertices())
            {
                System.out.println(v.getId());
                System.out.println(v.getProperty("name"));
            }

            System.out.println(graph.query().has("name").vertices());

            //        OGraphDatabase odb = ((OrientGraph)graph).getRawGraph();

            //        OGraphDatabase odb = new OGraphDatabase("local:flow").create();
            //        TransactionalGraph graph = new OrientGraph(odb);
        }
        catch (Exception e)
        {
            e.printStackTrace();

            graph.rollback();
        }

        graph.shutdown();
    }

    @AfterClass
    public static void teardown()
    {
        assumeThat(server, notNullValue());

        server.shutdown();
    }
}
