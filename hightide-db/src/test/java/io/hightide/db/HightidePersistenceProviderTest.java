package io.hightide.db;

import org.junit.Test;

import javax.persistence.Persistence;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class HightidePersistenceProviderTest {

    @Test
    public void testCreateEntityManagerFactory() throws Exception {
        Persistence.createEntityManagerFactory("default");
    }

}
