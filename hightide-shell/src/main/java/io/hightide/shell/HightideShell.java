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

package io.hightide.shell;

import io.hightide.commands.Command;
import jline.Terminal;
import jline.TerminalFactory;
import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;
import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.util.*;

import static io.hightide.shell.ShellColors.cyan;
import static io.hightide.shell.ShellColors.green;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class HightideShell {

    private static ServiceLoader<Command> commandsLoader = ServiceLoader.load(Command.class);

    private static Set<Command> commands = new LinkedHashSet<>();

    private HightideShell() {}

    private static class HightideShellHolder {
        public static final HightideShell INSTANCE = new HightideShell();
    }

    public static HightideShell instance() {
        return HightideShellHolder.INSTANCE;
    }

    public void start(String args[]) throws Exception {
        Terminal terminal = TerminalFactory.get();
        terminal.isAnsiSupported();
        printBanner();
        if (isNull(args) || args.length == 0) {
            console();
        } else {
            runCommand(args[0], Arrays.copyOfRange(args, 1, args.length));
        }
    }

    private void console() throws Exception {
        ConsoleReader reader = new ConsoleReader();
        reader.setBellEnabled(false);
//        List<Completer> completors = new LinkedList<>();
        List<String> commandNames = commands.stream().map(Command::getName).collect(toList());
        commandNames.add("exit");
        reader.addCompleter(new StringsCompleter(commandNames));
//        reader.addCompleter(new ArgumentCompleter(completors));
        String line;
        PrintWriter out = new PrintWriter(reader.getOutput());

        while ((line = readLine(reader, "")) != null) {
            if ("exit".equals(line)) {
                exit();
                return;
            } else {
                Boolean failed = !runCommand(line);
                if (failed) {
                    System.out
                            .println("Invalid command, For assistance press TAB or type \"help\" then hit ENTER.");
                }
            }
            out.flush();
        }
    }

    private Boolean runCommand(String line) throws Exception {
        final String[] lineTkns;
        final String cmdName;
        final String[] cmdArgs;
        if ((lineTkns = line.split("\\s+")).length > 1) {
            cmdName = lineTkns[0];
            cmdArgs = Arrays.copyOfRange(lineTkns, 1, lineTkns.length);
        } else {
            cmdName = line;
            cmdArgs = null;
        }
        return runCommand(cmdName, cmdArgs);
    }

    private Boolean runCommand(String cmdName, String[] cmdArgs) throws Exception {
        Optional<Command> cmd = getCommands().stream()
                .filter(c -> c.getName().equals(cmdName))
                .findFirst();
        if (cmd.isPresent()) {
            cmd.get().run(cmdArgs);
            return true;
        }
        return false;
    }

    private String readLine(ConsoleReader reader, String promptMessage) throws IOException {

        String line = reader.readLine(promptMessage + "\n" + green("hightide> "));
        return line.trim();
    }

    private void printBanner() {
        try {
            InputStream is = HightideShell.class.getResourceAsStream("/banner.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while (nonNull(line = br.readLine())) {
                System.out.println(cyan(line));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exit() {
        System.out.println("Bye, bye!");
        AnsiConsole.systemUninstall();
    }

    public static void main(String[] args) throws Exception {
        getCommands();
        HightideShell.instance().start(args);
    }

    public static Set<Command> getCommands() {
        if (commands.isEmpty()) {
            commandsLoader.iterator().forEachRemaining(commands::add);
        }
        return commands;
    }
}
