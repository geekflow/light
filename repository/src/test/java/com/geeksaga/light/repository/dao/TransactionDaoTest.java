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
package com.geeksaga.light.repository.dao;

import com.geeksaga.light.repository.Product;
import com.geeksaga.light.repository.dao.orientdb.TransactionDaoImpl;
import com.geeksaga.light.repository.entity.Transaction;
import com.geeksaga.light.util.SystemProperty;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

/**
 * @author geeksaga
 */
public class TransactionDaoTest
{
    private static final String DEFAULT_PATH = "/../../databases/";
    private static TransactionDao transactionDao;

    @BeforeClass
    public static void init()
    {
        System.setProperty("light.db.path", String.format("plocal:%s%s", System.getProperty("user.dir"), replaceWindowsSeparator(DEFAULT_PATH)));

        transactionDao = new TransactionDaoImpl(Product.NAME + "Test");
    }

    private static String replaceWindowsSeparator(String path)
    {
        if (SystemProperty.WINDOWS_OS && path != null)
        {
            return path.replace("\\", File.separator);
        }

        return path;
    }

    @Test
    public void testSave()
    {
        TransactionDao transactionDao = new TransactionDaoImpl(Product.NAME + "Test");

        Transaction transaction = new Transaction(1L);

        transactionDao.save(transaction);
    }

    @Test
    public void testFind()
    {
        Transaction transaction = new Transaction(1L);

        transactionDao.find(transaction);
        transactionDao.find(new Transaction(2L));
    }
}
