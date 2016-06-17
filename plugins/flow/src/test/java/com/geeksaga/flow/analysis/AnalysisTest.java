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
package com.geeksaga.flow.analysis;

import com.geeksaga.flow.store.StoreFactory;
import com.geeksaga.light.util.SystemProperty;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import target.TestClass;

import java.io.File;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author geeksaga
 */
public class AnalysisTest
{
    private static StoreFactory factory = null;
    private static final String DEFAULT_PATH = "/../../databases/";
    private static final String LOGFILE_PATH = "/src/test/resources/log4j2.xml";

    @BeforeClass
    public static void init()
    {
        System.setProperty("flow.db.path", String.format("plocal:%s%s", System.getProperty("user.dir"), replaceWindowsSeparator(DEFAULT_PATH)));
        System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, System.getProperty("user.dir") + replaceWindowsSeparator(LOGFILE_PATH));

        factory = StoreFactory.getInstance("flowtest");
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
    public void testAnalysisByClass()
    {
        Analysis.analysis(TestClass.class);

        assertThat(factory.store(AnalysisTest.class.getName(), "name", AnalysisTest.class.getName()), is(true));
    }
}
