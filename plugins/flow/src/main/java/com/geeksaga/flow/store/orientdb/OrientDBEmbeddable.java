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
package com.geeksaga.flow.store.orientdb;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author geeksaga
 */
public class OrientDBEmbeddable
{
    public static final String sun_boot_class_path = System.getProperty("sun.boot.class.path");
    public static final String line_separator = System.getProperty("line.separator");
    public static final String path_separator = System.getProperty("path.separator");

    private static String flowPath = null;

    public static void main(String[] args) throws Exception
    {
        OServer server = OServerMain.create();
        //        server.startup(OrientDBEmbeddable.class.getResourceAsStream("db.config"));
        server.startup(new File("src/main/resources/db.config")); // test

        System.out.println(sun_boot_class_path);

        server.activate();

        test();


        server.shutdown();
    }

    public static void test()
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

    public static String getConfigPath()
    {
        if (flowPath == null)
        {
            String path = sun_boot_class_path;

            try
            {
                int x = path.indexOf("geeksaga.flow.jar");
                if (x > -1)
                {
                    String path_sep = path_separator;
                    int x1 = path.lastIndexOf(path_sep, x);

                    if (x1 < 0)
                    {
                        flowPath = path.substring(0, x);
                    }
                    else
                    {
                        flowPath = path.substring(x1 + 1, x);
                    }
                }
            }
            catch (Exception exception)
            {
                // Logger.info(exception);
            }
        }

        return flowPath;
    }

    public void loadJarFile(File file, String name)
    {
        if (file != null && file.exists())
        {
            JarFile jfile = null;
            try
            {
                jfile = new JarFile(file);
                ZipEntry ent = jfile.getEntry(name);
                if (ent != null)
                {
                    InputStream fin = jfile.getInputStream(ent);

                    try
                    {
                        //                        load(fin);
                        fin.close();
                    }
                    finally
                    {
                        fin = null;
                    }
                }
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if (jfile != null)
                    {
                        jfile.close();
                        jfile = null;
                    }
                }
                catch (IOException e)
                {
                }
            }
        }
    }
}