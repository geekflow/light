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
package com.geeksaga.web.servlet.filter;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @author geeksaga
 */
@WebFilter("/*")
public class OrientDBFilter implements Filter
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException
    {
        try
        {
            chain.doFilter(request, response);
        }
        catch (IOException e)
        {
            throw new ServletException(e);
        }
        finally
        {
            ODatabaseDocumentInternal database = ODatabaseRecordThreadLocal.INSTANCE.getIfDefined();
            if (database != null)
            {
                database.close();
                ODatabaseRecordThreadLocal.INSTANCE.remove();
            }
        }
    }

    @Override
    public void destroy() {}
}