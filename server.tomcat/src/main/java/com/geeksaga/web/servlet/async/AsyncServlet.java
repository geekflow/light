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
package com.geeksaga.web.servlet.async;

import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author geeksaga
 */
@WebServlet(name = "AsyncServlet", urlPatterns = { "/consoleA" }, asyncSupported = true)
public class AsyncServlet extends HttpServlet
{
    private LightLogger logger;
    private Executor executor;

    public AsyncServlet()
    {
        logger = CommonLogger.getLogger(getClass().getName());
    }

    @Override
    public void init() throws ServletException
    {
        executor = Executors.newFixedThreadPool(30);
    }

    @Override
    protected void doGet(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException
    {
        final AsyncContext asyncContext = servletRequest.startAsync();

        executor.execute(() ->
        {
            HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();

            try (PrintWriter writer = response.getWriter())
            {
                writer.print("Ok");
            }
            catch (IOException e)
            {
                logger.info(e);
            }

            asyncContext.complete();
        });
    }
}