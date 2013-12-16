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

package io.hightide.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.*;
import io.undertow.util.HttpString;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class SessionHandler implements HttpHandler {

    private final String COUNT = "count";

    private final SessionCookieConfig sessionConfig = new SessionCookieConfig();

    private final SessionAttachmentHandler sessionHandler = new SessionAttachmentHandler(new InMemorySessionManager(), sessionConfig);

    public SessionHandler(HttpHandler next) {
        sessionHandler.setNext(exchange -> {
            final SessionManager manager = exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
            Session session = manager.getSession(exchange, sessionConfig);
            if (session == null) {
                session = manager.createSession(exchange, sessionConfig);
                session.setAttribute(COUNT, 0);
            }
            Integer count = (Integer) session.getAttribute(COUNT);
            exchange.getResponseHeaders().add(new HttpString(COUNT), count.toString());
            session.setAttribute(COUNT, ++count);
            next.handleRequest(exchange);
        });
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        sessionHandler.handleRequest(exchange);
    }
}
