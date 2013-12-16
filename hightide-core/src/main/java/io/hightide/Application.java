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

package io.hightide;

import io.hightide.logging.LoggingInitializer;
import io.hightide.server.Server;
import io.hightide.server.UndertowServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Main class of Hightide application.
 */
public final class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private static final Logger packageLogger = LoggerFactory.getLogger(Application.class.getPackage().getName());

    private static ClassLoader appClassLoader;

    private ApplicationConfig config;

    private ApplicationStage stage;

    private Server server;

    static {
        LoggingInitializer.init();
    }

    private Application(ApplicationContext appCtx) {
        config = appCtx.getConfig();
        stage = appCtx.getAppStage();

        printBanner();
        packageLogger.info("Hightide version " + this.getClass().getPackage().getImplementationVersion());

        if (isNull(appClassLoader)) {
            appClassLoader = this.getClass().getClassLoader();
        }

        server = new UndertowServer();
        server.start();

//        if (isNull(null)) {
//            logger.warn("No datasource configured; skipping database specific initialization.");
//        } else {
//            try {
//                DatabaseSynchronizer.run();
//            } catch (Exception e) {
//                logger.error("Database synchronization failed", e);
//            }
//        }
    }

    private void printBanner() {
        try {
            InputStream is = Application.class.getResourceAsStream("/banner.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while (nonNull(line = br.readLine())) {
                System.out.println(line);
            }
            br.close();
        } catch (IOException e) {
            logger.error("Failed to read banner file.", e);
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException {
        Application.run();
    }

    public static void run() {
        Application app = new Application(ApplicationContext.instance());
    }

    public static ClassLoader getAppClassLoader() {
        return appClassLoader;
    }

    public static void setAppClassLoader(ClassLoader classLoader) {
        appClassLoader = classLoader;
    }

}
