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
package com.geeksaga.web.servlet;

import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.geeksaga.light.repository.Product;
import com.geeksaga.light.repository.connect.RepositorySource;
import com.geeksaga.light.repository.dao.TransactionDao;
import com.geeksaga.light.repository.dao.orientdb.TransactionDaoImpl;
import com.geeksaga.light.repository.entity.Transaction;
import com.geeksaga.light.repository.util.IdentifierUtils;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ServletLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author geeksaga
 */
@WebServlet(name = "ConsoleServlet", urlPatterns = { "/console" })
public class ConsoleServlet extends HttpServlet
{
    private LightLogger logger;
    private RepositorySource repositorySource;
    private TransactionDao transactionDao;

    private static final String DEFAULT_PATH = "/../databases/";

    private PebbleEngine engine;

    public ConsoleServlet()
    {
        logger = CommonLogger.getLogger(getClass().getName());

        //        System.setProperty("light.db.url", String.format("memory:/%s/", Product.NAME.toLowerCase()));
        //        System.setProperty("light.db.url", String.format("plocal:.%s", DEFAULT_PATH));
        System.setProperty("light.db.url", String.format("plocal:%s", "/home/jennifer/jennifer5_simula/apache-tomcat-7.0.30-2/bin/databases/"));

        repositorySource = new RepositorySource(Product.NAME.toLowerCase());
        transactionDao = new TransactionDaoImpl(repositorySource);

//        engine = new PebbleEngine.Builder().loader(new ServletLoader(getServletContext())).build();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Transaction transaction1 = repositorySource.getObjectDatabaseTx().newInstance(Transaction.class, IdentifierUtils.nextLong());

        transactionDao.save(transaction1);

        ServletOutputStream out = response.getOutputStream();


        engine = new PebbleEngine.Builder().loader(new ServletLoader(getServletContext())).build();

        try
        {
            write(out);
        }
        catch (PebbleException e)
        {
            e.printStackTrace();
        }

        out.flush();
        out.close();
    }

    private void write(ServletOutputStream out) throws IOException, PebbleException
    {
        PebbleTemplate compiledTemplate = engine.getTemplate("transactionList.html");

        Writer writer = new StringWriter();

        Map<String, Object> context = new HashMap<>();

        compiledTemplate.evaluate(writer, context);

        for (Transaction transaction : transactionDao.findList())
        {
            logger.info("{}", transaction.toString());

            context.put("name", transaction.toString());

            //            out.write(transaction.toString().getBytes());
            //            out.write("<br />".getBytes());
        }

        compiledTemplate.evaluate(writer, context);

        String output = writer.toString();

        out.write(output.getBytes());

    }
}