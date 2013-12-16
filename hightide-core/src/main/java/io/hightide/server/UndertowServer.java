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
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static io.hightide.Handlers.*;
import static io.undertow.Handlers.predicate;
import static io.undertow.Handlers.resource;
import static io.undertow.predicate.Predicates.suffixs;

public class UndertowServer implements Server {

    private static final Logger logger = LoggerFactory.getLogger(UndertowServer.class);

    private final ApplicationConfig config = ApplicationContext.instance().getConfig();

    private Undertow server;

    public UndertowServer() {
        String host = config.getString("server.host");
        int port = config.getInt("server.port");
        String staticDir = config.getString("dirs.static");
        List<String> staticFileSuffixes = config.getStrings("app.static.suffixes");

        ResourceHandler staticHandler =
                resource(new FileResourceManager(new File(staticDir), 10));
        HttpHandler dynamicHandlers =
                monitor(errors(session(security(forms(route(invocation(redirect(respond()))))))));

        server = Undertow.builder()
                .addListener(port, host)
                .setHandler(
                    predicate(
                        suffixs(staticFileSuffixes.toArray(new String[0])), staticHandler,
                        dynamicHandlers
                    )
                )
                .build();

        logger.info("Hightide up and running! listening on port {}", port);
    }


    @Override
    public void start() {
        server.start();
    }

    @Override
    public void stop() {
        server.stop();
    }
}
