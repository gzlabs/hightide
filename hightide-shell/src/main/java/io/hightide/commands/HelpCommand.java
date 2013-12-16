/*
 * Copyright (c) 2013. Ground Zero Labs, Private Company.
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

package io.hightide.commands;

import io.hightide.shell.HightideShell;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static io.hightide.shell.ShellColors.*;
import static java.util.Objects.isNull;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class HelpCommand implements Command {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Show this help message.";
    }

    @Override
    public Optional<Set<CommandOption>> getOptions() {
        return Optional.empty();
    }

    @Override
    public String[] getUsages() {
        return new String[] { "help\nhelp <command_name>" };
    }

    @Override
    public void run(String... args) throws Exception {
        if (isNull(args) || args.length == 0) {
            printHelp();
        } else {
            String cmdName = args[0];
            Optional<Command> cmd = HightideShell.getCommands().stream()
                    .filter(c -> c.getName().equals(cmdName))
                    .findFirst();
            if (cmd.isPresent()) {
                printCmdHelp(cmd.get());
            }
        }
    }

    private void printHelp() {
        System.out.println("\n" + bold("Commands:"));
        Function<String, String> dynaTabs = (s) -> (s.length() - 3) > 3 ? "\t" : "\t\t";
        HightideShell.getCommands().stream()
                .map(cmd -> "  " + cyan(cmd.getName()) + dynaTabs.apply(cmd.getName()) +
                        cmd.getDescription().substring(0, cmd.getDescription().indexOf('.') != -1
                                ? cmd.getDescription().indexOf('.') + 1
                                : cmd.getDescription().length()))
                .forEach(System.out::println);
        System.out.println("  " + cyan("exit") + "\t\t...take a guess.");
        System.out.println("\nTry " + bold("help <command>") + " for help on a specific command.");
    }

    public static void printCmdHelp(Command cmd) {
        StringBuffer sb = new StringBuffer();
        sb.append("\n" + bold("Usage:") + "\n");
        sb.append("  " + cyan(String.join("\n  ", cmd.getUsages())) + "\n");
        cmd.getOptions().ifPresent(optns -> {
            sb.append("\n" + bold("Options:") + "\n");
            sb.append(optns);
        });
        sb.append("\n" + bold("Description:") + "\n");
        sb.append(cmd.getDescription());
        System.out.println(sb.toString());
        System.out.print(white());
    }
}
