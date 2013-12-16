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

import io.hightide.HightideExchange;
import io.hightide.HightideExchangeFactory;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class RedirectHandler implements HttpHandler {

    private HttpHandler next;

    public RedirectHandler(HttpHandler next) {
        this.next = next;
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        HightideExchange hightideExchange = HightideExchangeFactory.get(exchange);
        String location;
        if (null != (location = hightideExchange.getResolvedRoute().getRedirectTo())) {
            exchange.setResponseCode(302);
            exchange.getResponseHeaders().put(Headers.LOCATION, location);
            exchange.endExchange();
        } else {
            next.handleRequest(exchange);
        }
    }
}
