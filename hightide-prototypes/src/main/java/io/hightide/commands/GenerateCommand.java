package io.hightide.commands;

import java.util.Optional;
import java.util.Set;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class GenerateCommand implements Command {

    @Override
    public String getName() {
        return "generate";
    }

    @Override
    public Optional<Set<CommandOption>> getOptions() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Generate application artifacts.";
    }

    @Override
    public String[] getUsages() {
        return new String[0];
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
