package io.hightide;

import org.junit.Test;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class TemplateParserTest {

    @Test
    public void testParseTemplate() throws Exception {
        Map attrs = new HashMap();
        attrs.put("hightideVersion", "0.1-SNAPSHOT");
        attrs.put("groupId", "io.hightide");
        attrs.put("version", "0.1-SNAPSHOT");
        attrs.put("name", "template-parser-test");
        TemplateParser.parseTemplate(
                Paths.get("src/test/resources/build.gradle.tmpl"),
                Paths.get("src/test/resources/build.gradle"),
                attrs
        );
    }
}
