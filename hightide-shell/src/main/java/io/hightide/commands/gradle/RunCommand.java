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

package io.hightide.commands.gradle;

import io.hightide.commands.Command;
import io.hightide.commands.CommandOption;

import java.util.Optional;
import java.util.Set;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class RunCommand extends GradleSupport implements Command {

    @Override
    public String getName() {
        return "run";
    }

    @Override
    public Optional<Set<CommandOption>> getOptions() {
        return Optional.empty();
    }

    @Override
    public String getDescription() {
        return "Run application.";
    }

    @Override
    public String[] getUsages() {
        return new String[] {"run"};
    }

    @Override
    public void run(String... args) throws Exception {
        runTask("run");
    }
}
