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

import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;
import sun.management.VMManagement;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author geeksaga
 */
public class JavaProcess {
    private static final Logger logger = Logger.getLogger(JavaProcess.class.getName());

    public int getProcessId() {
        try {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            Field jvmField = runtimeMXBean.getClass().getDeclaredField("jvm");
            jvmField.setAccessible(true);
            VMManagement vmManagement = (VMManagement) jvmField.get(runtimeMXBean);
            Method getProcessIdMethod = vmManagement.getClass().getDeclaredMethod("getProcessId");
            getProcessIdMethod.setAccessible(true);
            return (Integer) getProcessIdMethod.invoke(vmManagement);
        } catch (Exception exception) {
            logger.log(Level.INFO, exception.getMessage(), exception);
        }

        return -1;
    }

    // supported JDK7
    public List<MonitoredVm> findList() throws Exception {
        List<MonitoredVm> list = new ArrayList<MonitoredVm>();

        MonitoredHost local = MonitoredHost.getMonitoredHost("localhost");
        Set<Integer> vmlist = new HashSet<Integer>(local.activeVms());
        for (int id : vmlist) {
            MonitoredVm vm = local.getMonitoredVm(new VmIdentifier("//" + id));
            String processname = MonitoredVmUtil.mainClass(vm, true);
            System.out.println(id + " [" + processname + "]");

            list.add(vm);
        }

        return list;
    }

    public static void main(String[] args) throws Exception {
        JavaProcess javaProcess = new JavaProcess();
        System.out.println(javaProcess.getProcessId());
//        javaProcess.findList();
    }
}
