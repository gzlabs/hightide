package io.hightide.commands.gradle;

import io.hightide.commands.Command;
import io.hightide.commands.CommandOption;

import java.util.Optional;
import java.util.Set;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class DebugCommand extends GradleSupport implements Command {

    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public Optional<Set<CommandOption>> getOptions() {
        return Optional.empty();
    }

    @Override
    public String getDescription() {
        return "Debug application.";
    }

    @Override
    public String[] getUsages() {
        return new String[] {"debug"};
    }

    @Override
    public void run(String... args) throws Exception {
        runTask("debug");
    }
}
