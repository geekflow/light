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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author geeksaga
 */
public class ClassSelectorTest
{
    private String[] target = { "javax.sql.DataSource getConnection(String, String)", //
            "test.trace.ServiceDummy param(1, 2, 3)", //
            "test.trace.ServiceDummy param(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", //
            "jlink.channel.soap.JLSOAPClntOBConnection", //
            "jlink.channel.soap.JLSOAPClntOBConnection JLSend", //
            "jlink.channel.soap.JLSOAPClntOBConnection JLSend(String, String)", //
            "jlink.channel.soap.JLSOAPClntOBConnection JLSend()", //
            "jlink.channel.soap.JLSOAPClntOBConnection JLRecv", //
            "org.apache.jasper.runtime.HttpJspBase", //
            "simula.P f2(double)", "java.lang.String", //
            "org.apache.jsp.example.time_005fcomponent_005fexample_jsp methodRun(J)V", //
            "target.TestMethods" };

    @Before
    public void setUp() {}

    @Test
    public void testCreate()
    {
        ClassSelector selector = ClassSelector.create(target);

        assertNotNull(selector.selectByClass(DataSource.class.getName()));
        assertNotNull(selector.selectByClass(TestMethods.class.getName()));
        assertNotNull(selector.selectByClass("simula.P"));

        MethodSelector methodSelector = selector.selectByClass("java.lang.String");

        assertThat(methodSelector.size(), is(0));
        assertNotNull(methodSelector);
    }

    @Test
    public void testFullDescriptionSelector()
    {
        ClassSelector selector = ClassSelector.create(new String[] { "org.apache.jsp.calltree_005fexample_jsp methodProfileExample(Ljava/lang/String;I)Ljava/lang/String;" });

        assertNotNull(selector.selectByClass("org.apache.jsp.calltree_005fexample_jsp"));

        MethodSelector methodSelector = selector.selectByClass("org.apache.jsp.calltree_005fexample_jsp");

        assertThat(methodSelector.isSelected("methodProfileExample", "(Ljava/lang/String;I)Ljava/lang/String;"), is(true));
    }

    @Test
    public void testDescriptionSelector()
    {
        ClassSelector selector = ClassSelector.create(new String[] { "javax.sql.DataSource getConnection(String, String)" });

        assertThat(selector.selectByClass("javax.sql.DataSource"), notNullValue());

        MethodSelector methodSelector = selector.selectByClass("javax.sql.DataSource");

        assertThat(methodSelector.size(), is(1));
        assertThat(methodSelector.isSelected("getConnection", "(String, String)"), is(true));
    }

    @Test
    public void testMultipleSelector()
    {
        ClassSelector selector = ClassSelector.create(target);
        selector.add("jlink.channel.soap.JLSOAPClntOBConnection", "Test");
        selector.add("jlink.channel.soap.JLSOAPClntOBConnection", "Test2");


        MethodSelector methodSelector = selector.selectByClass("jlink.channel.soap.JLSOAPClntOBConnection");

        assertThat(methodSelector, notNullValue());

        assertThat(methodSelector.size(), is(6));

        assertThat(methodSelector.isSelected("JLSend", null), is(true));
        assertThat(methodSelector.isSelected("JLSend", "(String, String)"), is(true));
        assertThat(methodSelector.isSelected("JLSend", "()"), is(true));
        assertThat(methodSelector.isSelected("JLRecv", "(String)"), is(false));
        assertThat(methodSelector.isSelected("JLRecv2", "(String)"), is(false));
        assertThat(methodSelector.isSelected("Test", "(String)"), is(false));
        assertThat(methodSelector.isSelected("Test2", "(String)"), is(false));
    }
}