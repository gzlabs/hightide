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
import io.hightide.HightideResponse;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.MetricsHandler;

import static io.hightide.Handlers.respond;
import static io.undertow.Handlers.path;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class MonitoringHandler implements HttpHandler {

    private HttpHandler pathHandler;

    public MonitoringHandler(HttpHandler nextHandler) {
        MetricsHandler mh = new MetricsHandler(nextHandler);
        this.pathHandler = path(mh).addPath("/stats", exchange -> {
            HightideExchange ex = HightideExchangeFactory.get(exchange);
            HightideResponse resp = new HightideResponse();
            resp.setReturnedObj(mh.getMetrics());
            ex.setResponse(resp);
            respond().handleRequest(exchange);
        });
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        pathHandler.handleRequest(exchange);
    }

}
