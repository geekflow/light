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
package com.geeksaga.web;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

/**
 * @author geeksaga
 */
public class Main
{
    public static void main(String[] args) throws Exception
    {
        String userDir = System.getProperty("user.dir") + File.separator + "server.tomcat";
        String webappDirLocation = userDir + File.separator +"src/main/webapp/";
        Tomcat tomcat = new Tomcat();

        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty())
        {
            webPort = "8080";
        }

        tomcat.setPort(Integer.valueOf(webPort));

        System.out.println("configuring app with basedir: " + new File(webappDirLocation).getAbsolutePath());

        StandardContext standardContext = (StandardContext) tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        File additionWebInfClasses = new File(userDir + File.separator + "build/classes");
        WebResourceRoot resourceRoot = new StandardRoot(standardContext);
        resourceRoot.addPreResources(new DirResourceSet(resourceRoot, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/"));
        standardContext.setResources(resourceRoot);

        tomcat.start();
        tomcat.getServer().await();
    }
}