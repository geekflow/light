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
import com.geeksaga.light.util.IdentifierUtils;
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

//    private static final String DEFAULT_PATH = "../databases";

    private PebbleEngine engine;

    public ConsoleServlet()
    {
        logger = CommonLogger.getLogger(getClass().getName());

        //        System.setProperty("light.db.url", String.format("memory:/%s/", Product.NAME.toLowerCase()));
//        System.setProperty("light.db.url", String.format("plocal:%s", DEFAULT_PATH));

        repositorySource = new RepositorySource(Product.NAME.toLowerCase());
        transactionDao = new TransactionDaoImpl(repositorySource);

        IdentifierUtils.seed(System.currentTimeMillis() ^ new Object().hashCode());
    }

    @Override
    public void init() throws ServletException
    {
        engine = new PebbleEngine.Builder().loader(new ServletLoader(getServletContext())).build();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Transaction transaction = repositorySource.getObjectDatabaseTx().newInstance(Transaction.class, IdentifierUtils.nextLong());
        transaction.setEndTime(System.currentTimeMillis());
        transaction.setElapsedTime(790827);
        transaction.setTransactionName("GeekSaga Light APM TEST - " + System.currentTimeMillis());

        transactionDao.save(transaction);

        try (ServletOutputStream out = response.getOutputStream())
        {
            write(out);
        }
        catch (PebbleException e)
        {
            logger.info(e);
        }
    }

    private void write(ServletOutputStream out) throws IOException, PebbleException
    {
        Writer writer = new StringWriter();
        Map<String, Object> context = new HashMap<>();
        context.put("transactions", transactionDao.findList());

        for(Transaction t : transactionDao.findList())
        {
            System.out.println(t);
        }

        PebbleTemplate compiledTemplate = engine.getTemplate("transactionList.html");
        compiledTemplate.evaluate(writer, context);

        String output = writer.toString();

        out.write(output.getBytes());
    }
}