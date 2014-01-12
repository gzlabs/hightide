package io.hightide.compiler;

import org.junit.Assert;
import org.junit.Test;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static java.util.Arrays.stream;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class CompilerPluginTest extends AbstractCompilerPluginTest {

    @Test
    public void testCompilerPluginInit() throws IOException {

        List<Diagnostic<? extends JavaFileObject>> diagnostics =
                compileTestCase(
                        "src/test/java/io/hightide/enjos/TestClass.java");
        assertCompilationSuccessful(diagnostics);

        try {
            URL classUrl = CompilerPluginTest.class.getClassLoader().getResource("");
            URLClassLoader loader = new URLClassLoader(new URL[]{ classUrl }, null);
            Class<?> aClass = loader.loadClass("io.hightide.enjos.TestClass");

            /** It should have created 3 getters, 2 setters (no setters for final properties), plus 4 existing methods. */
            Assert.assertEquals(9, aClass.getDeclaredMethods().length);

            /** There should be 3 private modifiers added in no-mod properties, one already private. */
            Assert.assertEquals(4,
                    stream(aClass.getDeclaredFields())
                    .filter(f -> Modifier.isPrivate(f.getModifiers()))
                    .count()
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
