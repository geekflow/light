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
package com.geeksaga.light.profiler.selector;

import org.junit.Before;
import org.junit.Test;
import target.TestMethods;

import javax.sql.DataSource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author geeksaga
 */
public class MethodSelectorTest
{
    private String[] target = { "javax.sql.RepositorySource getConnection(String, String)", //
            "test.trace.ServiceDummy param(1, 2, 3)", //
            "test.trace.ServiceDummy param(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", //
            "org.apache.http.impl.client.CloseableHttpClient execute(Lorg/apache/http/client/methods/HttpUriRequest;Lorg/apache/http/protocol/HttpContext;)Lorg/apache/http/client/methods/CloseableHttpResponse;", //
            "org.apache.http.impl.client.CloseableHttpClient execute(Lorg/apache/http/client/methods/HttpUriRequest;Lorg/apache/http/client/ResponseHandler;Lorg/apache/http/protocol/HttpContext;)Ljava/lang/Object;", //
            "org.apache.jsp.example.time_005fcomponent_005fexample_jsp methodRun(J)V", //
            "org.apache.jasper.runtime.HttpJspBase", //
            "org.apache.http.impl.client.CloseableHttpClient execute(Lorg/apache/http/client/methods/HttpUriRequest;Lorg/apache/http/client/ResponseHandler;Lorg/apache/http/protocol/HttpContext;)Ljava/lang/Object;", //
            "org.apache.jsp.example.time_005fcomponent_005fexample_jsp methodRun(J)V", //
            "target.TestMethods" };

    @Before
    public void setUp() {}

    @Test
    public void testIsSelected()
    {
        MethodSelector selector = ClassSelector.create(target).selectByClass(DataSource.class.getName());

        assertThat(selector, notNullValue());

        assertThat(selector.isSelected("getConnection", "()", true), is(false));
        assertThat(selector.isSelected("getConnection", "(String, String)"), is(true));

        selector = ClassSelector.create(target).selectByClass(TestMethods.class.getName());

        assertThat(selector.isSelected("param", "()", true), is(false));
        assertThat(selector.isSelected("param", "(1, 2, 3)"), is(true));

        assertThat(selector.isSelected("param", "()", true), is(false));
        assertThat(selector.isSelected("param", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"), is(true));

        selector = ClassSelector.create(target).selectByClass("org.apache.http.impl.client.CloseableHttpClient");

        assertThat(selector.isSelected("execute", "(Lorg/apache/http/client/methods/HttpUriRequest;Lorg/apache/http/protocol/HttpContext;)Lorg/apache/http/client/methods/CloseableHttpResponse;"), is(true));
        assertThat(selector.isSelected("execute", "(Lorg/apache/http/client/methods/HttpUriRequest;Lorg/apache/http/client/ResponseHandler;Lorg/apache/http/protocol/HttpContext;)Ljava/lang/Object;"), is(true));
        assertThat(selector.isSelected("execute", "(Lorg/apache/http/client/methods/HttpUriRequest;Lorg/apache/http/protocol/HttpContext;)Lorg/apache/http/HttpResponse;"), is(false));

        selector = ClassSelector.create(target).selectByClass("org.apache.jsp.example.time_005fcomponent_005fexample_jsp");

        assertThat(selector.isSelected("methodRun", "(J)V"), is(true));

        selector = ClassSelector.create(target).selectByClass("org.apache.jasper.runtime.HttpJspBase");

        assertThat(selector.isSelected("jspInit", "()V"), is(true));

        assertThat(selector.isSelected("jspInit", "()V", true), is(false));
    }
}