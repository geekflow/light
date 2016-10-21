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
package com.geeksaga.light.test;

import com.geeksaga.light.util.SystemProperty;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author geeksaga
 */
public class OtherTest
{
    private static final String DEFAULT_PATH = "/../databases/";

    private String replaceWindowsSeparator(String path)
    {
        if (SystemProperty.WINDOWS_OS && path != null)
        {
            return path.replace("/", File.separator);
        }

        return path;
    }

    @Test
    public void testOSSeparator()
    {
        if (SystemProperty.WINDOWS_OS)
        {
            assertThat(DEFAULT_PATH.replace("/", "\\"), is(replaceWindowsSeparator(DEFAULT_PATH)));
        }
        else
        {
            assertThat(DEFAULT_PATH, is(replaceWindowsSeparator(DEFAULT_PATH)));
        }
    }

    static class MyRunnable implements Runnable
    {
        @Override
        public void run()
        {
            ODatabaseDocumentTx tx = getDatabase("memory:Test", "admin", "admin");
            ODocument animal = tx.newInstance("Animal").field("name", "Gaudi").field("location", "Madrid");
            tx.save(animal);
            tx.close();
        }

        private ODatabaseDocumentTx getDatabase(String url, String userName, String password)
        {
            ODatabaseDocumentTx tx = new ODatabaseDocumentTx(url);
            if (!tx.exists())
            {
                tx.create();
                return tx;
            }
            return tx.open(userName, password);
        }
    }

    public static void main(String[] args)
    {
        new Thread(new MyRunnable()).start();
        new Thread(new MyRunnable()).start();
    }
}
