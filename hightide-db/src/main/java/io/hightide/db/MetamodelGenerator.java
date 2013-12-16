package io.hightide.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public interface MetamodelGenerator {

    public void generate(Connection connection) throws SQLException;

}
