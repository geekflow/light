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
package com.geeksaga.light.tools;

import com.geeksaga.light.tools.vm.VirtualMachineAttache;
import org.apache.commons.cli.*;


/**
 * @author geeksaga
 */
public class Main
{
    // private static LightLogger logger = CommonLogger.getLogger(Main.class.getName());

    public static void main(String[] args)
    {
        Main main = new Main();
        main.process(args);
    }

    private void process(String[] args)
    {
        VirtualMachineAttache virtualMachineAttache = new VirtualMachineAttache();

        CommandLine commandLine = parseOption(args);
        if (commandLine != null && commandLine.hasOption("a"))
        {
            virtualMachineAttache.loadAgentAfterAttach(commandLine.getOptionValue("a"));
        }
        else if (commandLine != null && commandLine.hasOption("p"))
        {
            virtualMachineAttache.showProcessList();
        }
        else if (commandLine != null && commandLine.hasOption("s"))
        {
            virtualMachineAttache.loadAgentAfterAttach();
        }
        else
        {
            usage();
        }
    }

    private CommandLine parseOption(String[] arguments)
    {
        CommandLineParser commandLineParser = new DefaultParser();

        try
        {
            return commandLineParser.parse(getOptions(), arguments);
        }
        catch (ParseException parseException)
        {
            System.err.println("Parsing failed.  Reason: " + parseException.getMessage());
        }

        return null;
    }

    private Options getOptions()
    {
        Option attachOption = new Option("a", "attach", true, "attach java process");
        attachOption.setArgs(Option.UNLIMITED_VALUES);
        attachOption.setArgName("process ID");

        Options options = new Options();
        options.addOption(attachOption);
        options.addOption(new Option("p", "process", false, "find java process"));
        options.addOption(new Option("s", "self", false, "self attach java process"));

        return options;
    }

    private void usage()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("light-tools.sh [-options] [args...]", getOptions());
    }
}
