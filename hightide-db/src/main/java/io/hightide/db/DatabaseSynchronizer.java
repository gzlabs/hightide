/*
 * Copyright (c) 2013. Ground Zero Labs, Private Company.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hightide.db;

import io.hightide.ApplicationConfig;
import io.hightide.ApplicationContext;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.FileSystemResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class DatabaseSynchronizer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSynchronizer.class);

    private ApplicationConfig config = ApplicationContext.instance().getConfig();

    private String dbName;

    public DatabaseSynchronizer(String dbName) {
        this.dbName = dbName;
    }

    public void run() throws Exception {
        logger.info("Checking if database is up-to-date...");
        ApplicationConfig dsConfig = config.getConfig("db." + dbName + ".datasource");
        String url = dsConfig.getString("jdbcUrl");
        String user = dsConfig.getString("user");
        String password = dsConfig.getString("password");
        Connection connection = DriverManager.getConnection(url + "?user=" + user + "&password=" + password);

        String changelog = config.getString("db." + dbName + ".changelog");
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase = new Liquibase(changelog, new FileSystemResourceAccessor(), database);
        List<ChangeSet> changeSets = liquibase.listUnrunChangeSets(null);
        if (changeSets.size() > 0) {
            logger.info("Applying {} pending changeset(s).", changeSets.size());
                        liquibase.update(null);

            //TODO: Abstract class generator away from here!
            Iterator<MetamodelGenerator> iterator = ServiceLoader.load(MetamodelGenerator.class).iterator();
            MetamodelGenerator gen = iterator.next();
            if (iterator.hasNext()) {
                throw new Exception("More than one ClassGenerators found on classpath.");
            }
            gen.generate(connection);

        } else {
            logger.info("All changesets already applied; no synchronization needed.");
        }
    }

    public static void main(String[] args) throws Exception {
        new DatabaseSynchronizer("default").run();
    }

}
