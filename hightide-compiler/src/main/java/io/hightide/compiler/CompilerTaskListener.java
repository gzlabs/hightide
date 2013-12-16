package io.hightide.compiler;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class CompilerTaskListener implements TaskListener {
    private final AstVisitor visitor;

    CompilerTaskListener(JavacTask task) {
        visitor = new AstVisitor(task);
    }

    @Override
    public void finished(TaskEvent taskEvent) {
    }

    @Override
    public void started(TaskEvent taskEvent) {
        if (taskEvent.getKind().equals(TaskEvent.Kind.ENTER)) {
            CompilationUnitTree compilationUnit = taskEvent.getCompilationUnit();
            visitor.scan(compilationUnit, null);
        }
    }
}

