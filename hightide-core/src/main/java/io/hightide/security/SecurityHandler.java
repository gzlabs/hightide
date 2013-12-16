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

package io.hightide.security;

import io.hightide.ApplicationConfig;
import io.hightide.ApplicationContext;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.handlers.AuthenticationCallHandler;
import io.undertow.security.handlers.AuthenticationConstraintHandler;
import io.undertow.security.handlers.AuthenticationMechanismsHandler;
import io.undertow.security.handlers.SecurityInitialHandler;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.impl.BasicAuthenticationMechanism;
import io.undertow.security.impl.CachedAuthenticatedSessionMechanism;
import io.undertow.security.impl.ClientCertAuthenticationMechanism;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.hightide.security.SecurityMode.NONE;
import static io.undertow.util.StatusCodes.FOUND;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class SecurityHandler implements HttpHandler {

    private static final SecurityMode securityMode = ApplicationContext.instance().getSecurityMode();

    private static final ApplicationConfig config = ApplicationContext.instance().getConfig();

    private final HttpHandler internalHandler;

    public SecurityHandler(HttpHandler next) {
        if (NONE.equals(securityMode)) {
            internalHandler = next::handleRequest;
        } else {
            final Map<String, char[]> users = new HashMap<>(2);
            users.put("userOne", "passwordOne".toCharArray());
            users.put("userTwo", "passwordTwo".toCharArray());

            final IdentityManager identityManager = new MapIdentityManager(users);

            internalHandler = addSecurity(exchange -> {

                SecurityContext securityContext = exchange.getAttachment(SecurityContext.ATTACHMENT_KEY);
                if (securityContext.isAuthenticated()) {
                    exchange.setResponseCode(FOUND);
                    exchange.endExchange();
                } else {
                    next.handleRequest(exchange);
                }
            }, identityManager);
        }
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        internalHandler.handleRequest(exchange);
    }

    private static HttpHandler addSecurity(final HttpHandler toWrap, final IdentityManager identityManager) {
        HttpHandler handler = new AuthenticationCallHandler(toWrap);
        handler = new AuthenticationConstraintHandler(handler);

        final List<AuthenticationMechanism> mechanisms = new ArrayList<>();

        switch (securityMode) {
            case BASIC:
                mechanisms.add(new BasicAuthenticationMechanism("HightideApp Realm"));
                break;
            case FORM:

                String loginUrl = config.getString("app.security.form.login_url");
                String errorUrl = config.getString("app.security.form.error_url");
                mechanisms.add(new CachedAuthenticatedSessionMechanism());
                mechanisms.add(new HightideFormAuthenticationMechanism("HightideApp Form Auth", loginUrl, errorUrl));
                break;
            case DIGEST:
                //mechanisms.add(new DigestAuthenticationMechanism());
                break;
            case CLIENT_CERT:
                mechanisms.add(new ClientCertAuthenticationMechanism());
                break;
            case GSSAPI:
                //mechanisms.add(new GSSAPIAuthenticationMechanism());
                break;
        }

        handler = new AuthenticationMechanismsHandler(handler, mechanisms);
        handler = new SecurityInitialHandler(AuthenticationMode.PRO_ACTIVE, identityManager, handler);
        return handler;
    }

}
