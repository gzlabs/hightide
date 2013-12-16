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

import io.undertow.security.impl.FormAuthenticationMechanism;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.Headers;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class HightideFormAuthenticationMechanism extends FormAuthenticationMechanism {

    public HightideFormAuthenticationMechanism(String name, String loginPage, String errorPage) {
        super(name, loginPage, errorPage);
    }

    public HightideFormAuthenticationMechanism(String name, String loginPage, String errorPage, String postLocation) {
        super(name, loginPage, errorPage, postLocation);
    }

    public HightideFormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage) {
        super(formParserFactory, name, loginPage, errorPage);
    }

    public HightideFormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage, String postLocation) {
        super(formParserFactory, name, loginPage, errorPage, postLocation);
    }

    @Override
    protected void handleRedirectBack(HttpServerExchange exchange) {
        exchange.setResponseCode(302);
        exchange.getResponseHeaders().add(Headers.LOCATION, "/posts");
        exchange.endExchange();
    }
}
