package io.hightide.db;

import org.junit.Test;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class DatabaseSynchronizerTest {

    @Test
    public void testDbSyncRun() throws Exception {
        /** XXX: Test should be run from hightide-db base dir */
        new DatabaseSynchronizer("default").run();
    }
}