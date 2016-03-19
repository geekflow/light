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
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
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
            logger.log(Level.INFO, attachNotSupportedException.getMessage(), attachNotSupportedException);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // supported JDK7
    public void show() {
        List<VirtualMachineDescriptor> vms = VirtualMachine.list();
        for (VirtualMachineDescriptor virtualMachineDescriptor : vms) {
            System.out.println("============ Show JVM: pid = " + virtualMachineDescriptor.id() + virtualMachineDescriptor.displayName());
            VirtualMachine virtualMachine = attach(virtualMachineDescriptor);
            if (virtualMachine != null) {
                System.out.println("     Java version = " + readSystemProperty(virtualMachine, "java.version"));
            }
        }
    }

    private VirtualMachine attach(VirtualMachineDescriptor virtualMachineDescriptor) {
        VirtualMachine virtualMachine = null;
        try {
            virtualMachine = VirtualMachine.attach(virtualMachineDescriptor);
            Properties props = new Properties();
            props.put("com.sun.management.jmxremote.port", "5000");
//            props.put("bootclasspath", "");
//            virtualMachine.startManagementAgent(props);
        } catch (AttachNotSupportedException attachNotSupportedException) {
            logger.log(Level.INFO, attachNotSupportedException.getMessage(), attachNotSupportedException);
        } catch (IOException ioException) {
            logger.log(Level.INFO, ioException.getMessage(), ioException);
        } finally {
            detach(virtualMachine);
        }

        return virtualMachine;
    }

    private String readSystemProperty(VirtualMachine virtualMachine, String propertyName) {
        String propertyValue = null;
        try {
            Properties systemProperties = virtualMachine.getSystemProperties();
            logger.info(systemProperties.toString());
            propertyValue = systemProperties.getProperty(propertyName);
        } catch (IOException ioException) {
            logger.log(Level.INFO, ioException.getMessage(), ioException);
        }

        return propertyValue;
    }

    private void detach(VirtualMachine virtualMachine) {
        if (virtualMachine != null) {
            try {
                virtualMachine.detach();
            } catch (IOException ioException) {
                logger.log(Level.INFO, ioException.getMessage(), ioException);
            }
        }
    }
}
