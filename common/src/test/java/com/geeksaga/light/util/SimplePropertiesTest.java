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
package com.geeksaga.light.util;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author geeksaga
 */
public class SimplePropertiesTest
{
    @Test
    public void testLoad()
    {
        SimpleProperties properties = new SimpleProperties("ignore.ini");

        assertThat(properties.getValueOrNull("empty"), nullValue());

        assertThat(properties.getValueOrNull("ignore_pattern"), is("com.geeksaga.light."));

        assertThat(Arrays.asList(properties.getValues("ignore_pattern")), containsInAnyOrder("com.geeksaga.light.", "java.lang.", "java.lang.String.toString()V"));

        assertThat(Arrays.asList(properties.getValues("ignore_pattern")).size(), is(3));
    }
}
