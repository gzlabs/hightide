package io.hightide.devassist;

import org.junit.Test;

import javax.annotation.processing.Processor;
import java.util.Collection;

import static java.util.Arrays.asList;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class AnnotationProcessorTest extends AbstractAnnotationProcessorTest {

    @Override
    protected Collection<Processor> getProcessors() {
        return asList((Processor) new AnnotationProcessor());
    }

    @Test
    public void testAnnotatedClass() {
//        List<Diagnostic<? extends JavaFileObject>> diagnostics =
//                compileTestCase("src/test/java/io/hightide/devassist/TestClass.java");
//        diagnostics.stream().filter(d -> !d.getKind().equals(ERROR)).forEach(System.out::println);
//        assertCompilationSuccessful(diagnostics);
    }

}
