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

import com.geeksaga.light.logger.CommonLogger;
import com.geeksaga.light.logger.LightLogger;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author geeksaga
 */
public class VirtualMachineWrapper
{
    private static final LightLogger logger = CommonLogger.getLogger(VirtualMachineWrapper.class.getName());

    public void loadAgentAfterAttach(String agent)
    {
        loadAgentAfterAttach(getProcessID(), agent, null);
    }

    public void loadAgentAfterAttach(String processId, String agent)
    {
        loadAgentAfterAttach(processId, agent, null);
    }

    public void loadAgentAfterAttach(String processId, String agent, String options)
    {
        logger.info("dynamically loading javaagent for process = " + processId);

        try
        {
            VirtualMachine virtualMachine = VirtualMachine.attach(processId);
            virtualMachine.loadAgent(agent, options);
        }
        catch (AttachNotSupportedException attachNotSupportedException)
        {
            logger.info(attachNotSupportedException);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    // supported JDK7
    public void showProcessList()
    {
        List<VirtualMachineDescriptor> virtualMachineDescriptorList = VirtualMachine.list();

        String version = null;
        for (VirtualMachineDescriptor virtualMachineDescriptor : virtualMachineDescriptorList)
        {
            VirtualMachine virtualMachine = attach(virtualMachineDescriptor);
            if (virtualMachine != null)
            {
                version = readSystemProperty(virtualMachine, "java.version");
            }

            logger.info("Show JVM : pid = {}, DisplayName = {}, Java Version = {}", virtualMachineDescriptor.id(), virtualMachineDescriptor.displayName(), version);

            detach(virtualMachine);
        }
    }

    private String getProcessID()
    {
        return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }

    private VirtualMachine attach(VirtualMachineDescriptor virtualMachineDescriptor)
    {
        VirtualMachine virtualMachine = null;

        try
        {
            virtualMachine = VirtualMachine.attach(virtualMachineDescriptor);
            //            Properties props = new Properties();
            //            props.put("com.sun.management.jmxremote.port", "5000");
            //            props.put("bootclasspath", "");
            //            virtualMachine.startManagementAgent(props);
        }
        catch (AttachNotSupportedException | IOException attachNotSupportedException)
        {
            logger.info(attachNotSupportedException);
        }

        return virtualMachine;
    }

    private String readSystemProperty(VirtualMachine virtualMachine, String propertyName)
    {
        String propertyValue = null;
        try
        {
            if (virtualMachine != null)
            {
                Properties systemProperties = virtualMachine.getSystemProperties();
                propertyValue = systemProperties.getProperty(propertyName);

                Enumeration enumeration = systemProperties.keys();
                while (enumeration.hasMoreElements())
                {
                    String key = (String) enumeration.nextElement();
                    //                    logger.info(key + " = " + systemProperties.getProperty(key));
                }
            }
        }
        catch (IOException ioException)
        {
            logger.info(ioException);
        }

        return propertyValue;
    }

    private void detach(VirtualMachine virtualMachine)
    {
        if (virtualMachine != null)
        {
            try
            {
                virtualMachine.detach();
            }
            catch (IOException ioException)
            {
                logger.info(ioException);
            }
        }
    }
}
