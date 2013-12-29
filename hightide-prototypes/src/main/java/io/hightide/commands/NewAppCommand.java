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

import io.hightide.shell.ShellColors;

import java.util.Optional;
import java.util.Set;

import static io.hightide.Prototypes.generateApp;
import static io.hightide.shell.ShellColors.bold;
import static io.hightide.shell.ShellColors.cyan;
import static java.util.Objects.isNull;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class NewAppCommand implements Command {

    String[] usages = new String[] {
        "new <app_name>", // + "\t\tCreate a new application with default template.\n");
        "new <app_name> <template_type>:<template_name>" // + "\tCreates a new application using provided template.");
    };

    @Override
    public String getName() {
        return "new";
    }

    @Override
    public Optional<Set<CommandOption>> getOptions() {
        return Optional.empty();
    }

    @Override
    public String getDescription() {
        return "Creates a new Hightide application. If template is not defined the default template will be used.\n\n"
        + "Supported template types are:\n"
        + cyan("  file") + "\t\tA template archive from local files system\n\t\t"
                + ShellColors.yellow("(e.g. 'file:hightide-app-prototype.tar.gz').\n")
        //+ "\tgit\tA git repository (e.g. 'git:hightide-app-prototype.tar.gz')\n";
        + cyan("  github") + "\tA github.com repository containing the app template\n\t\t"
                + ShellColors.yellow("(e.g. 'github:gzlabs/hightide-app-prototype').\n")
        + cyan("  bibucket") + "\tA bitbucket.org repository containing the app template\n\t\t"
                + ShellColors.yellow("(e.g. 'bitbucket:groundzerolabs/hightide-app-prototype').\n");
    }

    @Override
    public String[] getUsages() {
        return usages;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println();
        // Check if one argument provided
        if (isNull(args) || args.length == 0) {
            System.out.println("Invalid usage of " + cyan(this.getName())
                    + " command; If you don't know what your doing try " + bold("help new") + ".");
            return;
        }

        String appName = args[0];
        String template = null;
        if (args.length > 1) {
            template = args[1];
        }
        if (isNull(template)) {
            System.out.println("Using default template to create application '" + appName + "'.");
            generateApp(appName);
        } else {
            generateApp(appName, template);
        }
        System.out.println("Application '" + appName + "' created.");

    }

}
