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

package io.hightide.server;

import io.hightide.ApplicationConfig;
import io.hightide.ApplicationContext;
import io.hightide.route.RoutesManager;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.util.List;

import static io.hightide.Handlers.*;
import static io.undertow.Handlers.predicate;
import static io.undertow.Handlers.resource;
import static io.undertow.predicate.Predicates.suffixs;
import static java.util.Objects.isNull;

public class UndertowServer implements Server {

    private static final Logger logger = LoggerFactory.getLogger(UndertowServer.class);

    private final ApplicationConfig config = ApplicationContext.instance().getConfig();

    private Undertow server;

    private String host;

    private int port;

    private String staticDir;

    public UndertowServer() {
        host = config.getString("server.host");
        port = config.getInt("server.port");
        staticDir = config.getString("dirs.static");

        List<String> staticFileSuffixes = config.getStrings("app.static.suffixes");

        ResourceManager resourceManager = new FileResourceManager(new File(staticDir), 10) {
            ClassPathResourceManager cprm = new ClassPathResourceManager(this.getClass().getClassLoader(), "");

            @Override
            public Resource getResource(String path) {
                Resource resource = super.getResource(path);
                if (isNull(resource)) {
                    try {
                        resource = cprm.getResource(path);
                    } catch (IOException e) {
                        resource = null;
                    }
                }
                return resource;
            }
        };

        RoutesManager routesManager = ApplicationContext.instance().getRoutesManager();

        HttpHandler dynamicHandlers =
                monitor(errors(session(security(forms(route(routesManager, invocation(redirect(respond()))))))));

        server = Undertow.builder()
                .addListener(port, host)
                .setHandler(
                    predicate(
                        suffixs(staticFileSuffixes.toArray(new String[0])), resource(resourceManager),
                        dynamicHandlers
                    )
                )
                .build();

    }


    @Override
    public void start() {
        try {
            server.start();
            logger.info("Hightide up and running! listening on port {}.", port);
        } catch (Exception e) {
            if (e.getCause() instanceof BindException) {
                logger.error("Failed to start server; address {} already in use.", port);
            } else {
                logger.error("Failed to start server", e);
            }
            System.exit(0);
        }
    }

    @Override
    public void stop() {
        server.stop();
    }
}
