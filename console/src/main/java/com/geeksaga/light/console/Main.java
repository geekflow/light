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
package com.geeksaga.light.console;


import org.apache.commons.cli.*;

import java.io.Console;

/**
 * @author geeksaga
 */
public class Main
{
    private static final String NEW_LINE = System.getProperty("line.separator");

    public static void main(String[] args) throws Exception
    {
        Main main = new Main();
        main.parseOption(args);

        //        main.doConsole();

        main.usage();
    }

    private void doConsole() throws Exception
    {
        Console console = System.console();

        String command = console.readLine("> ");

        System.out.println(command);
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
        Option bootStrapOption = new Option("b", "bootstrap", true, "library append to class loader of bootstrap");
        bootStrapOption.setArgs(Option.UNLIMITED_VALUES);
        bootStrapOption.setArgName("library path");

        Option attachOption = new Option("a", "attach", true, "attach java process");
        attachOption.setArgs(Option.UNLIMITED_VALUES);
        attachOption.setArgName("process ID");

        Option processOption = new Option("p", "process", false, "find java process");

        Options options = new Options();
        options.addOption(bootStrapOption);
        options.addOption(attachOption);
        options.addOption(processOption);

        return options;
    }

    private void usage()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("light-console.sh [-options] [args...]", getOptions());
    }
}
