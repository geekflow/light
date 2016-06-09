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
package com.geeksaga.light.tools.vm;

import org.junit.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;

/**
 * @author geeksaga
 */
public class AttachMainTest
{
    @Test
    public void testList()
    {
        AttachMain main = new AttachMain();
        main.show();//attach();
    }

    @Test
    public void testFindLastAgentJarOrNull()
    {
        String CLASS_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "AGENT_HOME";

        System.out.println(CLASS_PATH);
        AttachMain main = new AttachMain();
        assertThat(main.findLastAgentJarOrNull(), isEmptyOrNullString());
        assertThat(main.findLastAgentJarOrNull(CLASS_PATH), is("0.0.2"));
    }
}
