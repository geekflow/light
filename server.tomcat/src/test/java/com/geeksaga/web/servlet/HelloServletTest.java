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

import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author geeksaga
 */
public class HelloServletTest
{
    @Test
    public void testDoGet() throws ServletException, IOException
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);

        when(response.getStatus()).thenReturn(200);

        when(response.getOutputStream()).thenReturn(new ServletOutputStream()
        {
            @Override
            public void write(final byte b[]) throws IOException
            {
                assertThat(new String(b), is("hello"));
            }

            @Override
            public void write(final int i) throws IOException
            {}

            @Override
            public boolean isReady()
            {
                return false;
            }

            @Override
            public void setWriteListener(final WriteListener writeListener)
            {}
        });

        HelloServlet servlet = new HelloServlet();
        servlet.doGet(request, response);

        assertThat(response.getStatus(), is(200));
    }
}
