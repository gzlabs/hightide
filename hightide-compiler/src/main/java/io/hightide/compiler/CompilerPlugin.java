package io.hightide.compiler;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class CompilerPlugin implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(CompilerPlugin.class);

    @Override
    public String getName() {
        return "HightideCompilerPlugin";
    }

    @Override
    public void init(JavacTask task, String... args) {
        logger.info("Hightide compiler plugin enabled");
        task.setTaskListener(new CompilerTaskListener(task));
    }

}
