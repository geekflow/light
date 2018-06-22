/*
 * Copyright 2018 GeekSaga.
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
package com.geeksaga.light.agent.elastic;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class ElasticTest
{
    @Test
    public void testPUT() throws IOException
    {
        RestClient restClient = RestClient.builder(new HttpHost("127.0.0.1", 9200)).build();

        Response response;

        HttpEntity entity = new NStringEntity(
                "{\n" +
                        "    \"application\" : \"" + "Test" + "\",\n" +
                        "    \"startTime\" : \"" + (System.currentTimeMillis() -1000 )+ "\",\n" +
                        "    \"endTime\" : \"" + System.currentTimeMillis() + "\",\n" +
                        "    \"elapsedTime\" : \"" + 1000 + "\"\n" +
                        "}",
                ContentType.APPLICATION_JSON
        );

        System.out.println(entity.toString());

        response = restClient.performRequest("PUT", "test_index/test_type/1234", Collections.<String, String>emptyMap(), entity);

        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println(statusCode);
    }
}
