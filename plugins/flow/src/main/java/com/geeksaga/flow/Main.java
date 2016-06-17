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
package com.geeksaga.flow;

import com.geeksaga.flow.command.CommandFactory;
import com.geeksaga.flow.command.CommandManager;

import java.io.Console;

/**
 * @author geeksaga
 */
public class Main
{
    private static final String NEW_LINE = System.getProperty("line.separator");

    public static void main(String[] args) throws Exception
    {
        System.out.println(String.format("GeekSaga - %s 0.1v", Product.NAME));

        Main main = new Main();
        main.doConsole();
    }

    private void doConsole() throws Exception
    {
        Console console = System.console();

        if (console != null) // IDE not support
        {
            String className = console.readLine("> ");

            CommandManager commandManager = new CommandManager();

            while (commandManager.execute(new CommandFactory().createCommand(className)))
            {
                console.printf(NEW_LINE);

                className = console.readLine("> ");
            }
        }
    }
}
