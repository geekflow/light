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
package com.geeksaga.light.console;

import org.apache.commons.cli.CommandLine;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author geeksaga
 */
public class MainTest {

    @Test
    public void testDummy() {
        Main main = new Main();
        CommandLine command = main.parseOption(new String[] {"light.sh", "-a1234", "-p"});

        assertThat(command.hasOption("a"), is(true));
        assertThat(command.getOptionValue("a"), is("1234"));


        System.out.println(command.getOptionValue("a"));
    }
}
