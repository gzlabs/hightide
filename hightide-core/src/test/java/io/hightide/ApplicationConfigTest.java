package io.hightide;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class ApplicationConfigTest {

    private ApplicationConfig config = new ApplicationConfig();

    @Test
    public void testGetApplicationConfigKeyFromReferenceConf() throws Exception {
        assertEquals("src/test/resources/app", config.getString("dirs.src"));
    }

    @Test
    public void testGetApplicationConfigKeyFromApplicationConf() throws Exception {
        assertEquals("static", config.getString("dirs.static"));
    }

    @Test
    public void testGetSubConfig() throws Exception {
        ApplicationConfig subConf = config.getConfig("dirs");
        assertEquals(6, subConf.count().intValue());
    }
}
