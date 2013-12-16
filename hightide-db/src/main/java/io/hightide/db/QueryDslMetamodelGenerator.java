package io.hightide.db;

import io.hightide.ApplicationConfig;
import io.hightide.ApplicationContext;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class QueryDslMetamodelGenerator implements MetamodelGenerator {

    public void generate(Connection connection) throws SQLException {
        ApplicationConfig config = ApplicationContext.instance().getConfig();
        String packageName = config.getString("app.packages.resources");
        File outputDir = new File(config.getString("dirs.gen-src"));

//        MetaDataExporter exporter = new MetaDataExporter();
//        exporter.setPackageName(packageName);
//        exporter.setTargetFolder(outputDir);
//        exporter.export(connection.getMetaData());
    }

}
