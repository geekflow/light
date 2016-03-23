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
package com.geeksaga.light.agent.util;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author geeksaga
 */
public class FieldNameMapTest {
    public static final String FIELD_1 = "FIELD_1";
    public static final String FIELD_2 = "FIELD_2";
    public static final String FIELD_3 = "FIELD_3";

    @Test
    public void testToMap() {
        FieldNameMap names = FieldNameMap.toMap(FieldNameMapTest.class);

        assertThat(names.hasName(FIELD_1), is(true));
    }
}
