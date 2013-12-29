package io.hightide;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class TemplateFetcherTest {
    private static final String DEFAULT_APP_TEMPLATE = "github:gzlabs/hightide-app-prototype";

    @Test
    public void testGetPrototype() throws Exception {
        String currentDir = System.getProperty("user.dir");
        Path tempDir = Files.createTempDirectory(Paths.get(currentDir), null);
        TemplateFetcher.getPrototype(tempDir, DEFAULT_APP_TEMPLATE);
    }
}
