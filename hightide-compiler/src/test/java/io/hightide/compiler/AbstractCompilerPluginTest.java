package io.hightide.compiler;

import org.junit.Assert;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class AbstractCompilerPluginTest {
    private static final String SOURCE_FILE_SUFFIX = ".java";
    private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();

    protected List<Diagnostic<? extends JavaFileObject>> compileTestCase(
            String... compilationUnitPaths) throws IOException {
        assert (compilationUnitPaths != null);


        DiagnosticCollector<JavaFileObject> diagnosticCollector =
                new DiagnosticCollector<>();
        StandardJavaFileManager fileManager =
                COMPILER.getStandardFileManager(diagnosticCollector, null, null);
        fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
                Arrays.asList(new File(this.getClass().getClassLoader().getResource("").getPath())));
        JavaCompiler.CompilationTask task = COMPILER.getTask(null, fileManager, diagnosticCollector,
                Arrays.asList("-Xplugin:HightideCompilerPlugin"), null,
                fileManager.getJavaFileObjects(compilationUnitPaths));
        task.call();

        try {
            fileManager.close();
        } catch (IOException exception) {
        }

        return diagnosticCollector.getDiagnostics();
    }

    /**
     * Asserts that the compilation produced no errors, i.e. no diagnostics of
     * type {@link javax.tools.Diagnostic.Kind#ERROR}.
     *
     * @param diagnostics the result of the compilation
     * @see #assertCompilationReturned(javax.tools.Diagnostic.Kind, long, List)
     * @see #assertCompilationReturned(javax.tools.Diagnostic.Kind[], long[], List)
     */
    protected static void assertCompilationSuccessful(
            List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        assert (diagnostics != null);

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
            Assert.assertFalse("Expected no errors", diagnostic.getKind().equals(Diagnostic.Kind.ERROR));
        }

    }

    /**
     * Asserts that the compilation produced results of the following
     * {@link javax.tools.Diagnostic.Kind Kinds} at the given line numbers, where the <em>n</em>th kind
     * is expected at the <em>n</em>th line number.
     * <p/>
     * Does not check that these is the <em>only</em> diagnostic kinds returned!
     *
     * @param expectedDiagnosticKinds the kinds of diagnostic expected
     * @param expectedLineNumbers     the line numbers at which the diagnostics are expected
     * @param diagnostics             the result of the compilation
     * @see #assertCompilationSuccessful(List)
     * @see #assertCompilationReturned(javax.tools.Diagnostic.Kind, long, List)
     */
    protected static void assertCompilationReturned(
            Diagnostic.Kind[] expectedDiagnosticKinds, long[] expectedLineNumbers,
            List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        assert ((expectedDiagnosticKinds != null) && (expectedLineNumbers != null)
                && (expectedDiagnosticKinds.length == expectedLineNumbers.length));

        for (int i = 0; i < expectedDiagnosticKinds.length; i++) {
            assertCompilationReturned(expectedDiagnosticKinds[i], expectedLineNumbers[i],
                    diagnostics);
        }

    }

    /**
     * Asserts that the compilation produced a result of the following
     * {@link javax.tools.Diagnostic.Kind} at the given line number.
     * <p/>
     * Does not check that this is the <em>only</em> diagnostic kind returned!
     *
     * @param expectedDiagnosticKind the kind of diagnostic expected
     * @param expectedLineNumber     the line number at which the diagnostic is expected
     * @param diagnostics            the result of the compilation
     * @see #assertCompilationSuccessful(List)
     * @see #assertCompilationReturned(javax.tools.Diagnostic.Kind[], long[], List)
     */
    protected static void assertCompilationReturned(
            Diagnostic.Kind expectedDiagnosticKind, long expectedLineNumber,
            List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        assert ((expectedDiagnosticKind != null) && (diagnostics != null));
        boolean expectedDiagnosticFound = false;

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {

            if (diagnostic.getKind().equals(expectedDiagnosticKind)
                    && (diagnostic.getLineNumber() == expectedLineNumber)) {
                expectedDiagnosticFound = true;
            }

        }

        Assert.assertTrue("Expected a result of kind " + expectedDiagnosticKind
                + " at line " + expectedLineNumber, expectedDiagnosticFound);
    }
}
