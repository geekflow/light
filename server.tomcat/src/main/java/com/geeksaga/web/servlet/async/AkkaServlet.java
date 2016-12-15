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

//import akka.actor.ActorSystem;
import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @author geeksaga
 */
@WebServlet(name = "AkkaServlet", urlPatterns = { "/consoleAKKA" }, asyncSupported = true)
public class AkkaServlet extends HttpServlet
{
    private LightLogger logger;

    public AkkaServlet()
    {
        logger = CommonLogger.getLogger(getClass().getName());
    }

    @Override
    public void init() throws ServletException {}

    @Override
    protected void doGet(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException
    {
//        final AsyncContext asyncContext = servletRequest.startAsync();
//        final ActorSystem actorSystem = (ActorSystem) servletRequest.getServletContext().getAttribute("ActorSystem");
//
//        Enumeration<String> enums = servletRequest.getServletContext().getAttributeNames();
//
//        while (enums.hasMoreElements())
//        {
//            System.out.println(enums.nextElement());
//        }

        //        actorSystem.actorOf(Props.create(AskActor.class, asyncContext));
    }

    //    private static class TestActor extends UntypedActor
    //    {
    //        @Override
    //        public void onReceive(Object msg) throws Exception
    //        {
    //            if (msg == "Test!!!")
    //            {
    //                getSender().tell("Ok", getSelf());
    //            }
    //            else
    //            {
    //                unhandled(msg);
    //            }
    //        }
    //    }

    //    private static class AskActor extends UntypedActor
    //    {
    //        private final AsyncContext asyncContext;
    //
    //        public AskActor(AsyncContext asyncContext)
    //        {
    //            this.asyncContext = asyncContext;
    //            //            ActorRef testActor = getContext().actorOf(Props.create(TestActor.class), "TestActor");
    //            ActorRef actorRef = getContext().actorOf(Props.create(TestActor.class));
    //            getContext().watch(actorRef);
    //            getContext().setReceiveTimeout(Duration.create("5 seconds"));
    //            actorRef.tell("Test!!!", getSelf());
    //        }
    //
    //        @Override
    //        public void onReceive(Object msg) throws IOException
    //        {
    //            HttpServletResponse resp = (HttpServletResponse) asyncContext.getResponse();
    //            if (msg instanceof ReceiveTimeout)
    //            {
    //                getContext().stop(getSelf());
    //                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Timeout");
    //                asyncContext.complete();
    //            }
    //            else if (msg instanceof Terminated)
    //            {
    //                getContext().stop(getSelf());
    //                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpectedly Stopped");
    //                asyncContext.complete();
    //            }
    //            else if (msg instanceof String)
    //            {
    //                getContext().stop(getSelf());
    //                resp.setContentType("text/plain");
    //                try (PrintWriter writer = resp.getWriter())
    //                {
    //                    writer.print("Ok");
    //                }
    //                asyncContext.complete();
    //            }
    //            else
    //            {
    //                unhandled(msg);
    //            }
    //        }
    //    }
}