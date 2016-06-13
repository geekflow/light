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
package com.geeksaga.light.collector

import org.junit.Assert._
import org.junit.Test

import scala.collection.mutable.ListBuffer

import org.hamcrest.Matchers.is

/**
  * @author geeksaga
  */
class MainTest {
  @Test def +() {
    val list = new ListBuffer[String]
    list += "one"

    assertEquals("Wrong list state", ListBuffer("one"), list)

    assertThat(list, is(ListBuffer("one")))
  }

  @Test def testEmptyTrue(): Unit = {
    val list = new ListBuffer[String]

    assertTrue("list should be empty", list.isEmpty)
  }

  @Test def testEmptyFalse(): Unit = {
    val list = ListBuffer("one")

    assertFalse("list should not be empty", list.isEmpty)
  }
}
