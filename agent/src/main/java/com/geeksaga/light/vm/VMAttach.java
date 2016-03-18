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
package com.geeksaga.light.vm;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

/**
 * @author geeksaga
 */
public class VMAttach {
    private static final Logger logger = Logger.getLogger(VMAttach.class.getName());

    public void loadAgent(String agent) {
        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');
        String pid = nameOfRunningVM.substring(0, p);

        logger.info("dynamically loading javaagent = " + pid);

        try {
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(agent, null);
            vm.detach();
        } catch (AttachNotSupportedException attachNotSupportedException) {
            attachNotSupportedException.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
