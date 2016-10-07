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
package com.geeksaga.light.repository;

import com.geeksaga.light.agent.core.ActiveObject;
import com.geeksaga.light.agent.trace.MethodInfo;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;

import static com.geeksaga.light.repository.util.ModuleThreadFactory.createFactory;

/**
 * @author geeksaga
 */
public class RepositoryWorkerTest
{
    private BlockingQueue<ActiveObject> queue = new ArrayBlockingQueue<ActiveObject>(100);

    @Test
    public void testExecute() throws InterruptedException
    {
        Executors.newSingleThreadExecutor(createFactory(Product.NAME + getClass().getName(), Thread.NORM_PRIORITY)).execute(new RepositoryWorker(queue));

        ActiveObject activeObject = new ActiveObject(Thread.currentThread(), new MethodInfo(getClass().getName(), getClass().getSimpleName()));
        activeObject.setStartTimeMillis(System.currentTimeMillis());

        queue.put(activeObject);
    }
}