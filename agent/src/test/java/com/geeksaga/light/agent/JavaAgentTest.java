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
package com.geeksaga.light.agent;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author geeksaga
 */
public class JavaAgentTest {

    @Test
    public void testDuplicateInitializeCalls()
    {
        assertThat(JavaAgent.STATUS.get(), is(false));

        JavaAgent.premain("", new DummyInstrumentation());

        for(int i=0; i<3; i++) {
            assertThat(JavaAgent.STATUS.get(), is(true));

            if(!JavaAgent.STATUS.get()) {
                JavaAgent.premain("", new DummyInstrumentation());

                fail();
            }
        }
    }
}
