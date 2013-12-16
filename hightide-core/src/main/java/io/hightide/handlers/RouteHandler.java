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
import io.hightide.HightideRequest;
import io.hightide.route.Route;
import io.hightide.route.RouteParsingException;
import io.hightide.route.RoutesParser;
import io.hightide.route.UnmatchedRouteException;
import io.undertow.server.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.regex.Matcher;

import static java.util.Objects.nonNull;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public final class RouteHandler extends HightideHandler {

    private static final Logger logger = LoggerFactory.getLogger(RouteHandler.class);

    private LinkedHashSet<Route> routes;

    public RouteHandler(HttpHandler next) {
        super(next);
        loadRoutes();
    }

    public void loadRoutes() {
        try {
            routes = RoutesParser.parse();
        } catch (RouteParsingException e) {
            logger.error("Failed to load routes!", e);
        }
    }

    @Override
    public void handleRequest(HightideExchange exchange) throws Exception {
        Map<String, Deque<String>> reqParams;
        HightideRequest request = exchange.getRequest();
        if (null != (reqParams = request.getReqParams())) {
            if (reqParams.containsKey("_method")) {
                request.setMethod(reqParams.get("_method").getFirst());
            }
        }
        request.setMatchingPath(request.getPath().concat(".").concat(request.getMethod().toLowerCase()));
        Route route = resolve(request);
        logger.debug("Request {} resolved to route: {}", request, route);
        exchange.setResolvedRoute(route);
    }

    private Route resolve(HightideRequest request) throws UnmatchedRouteException {
        return routes.stream()
                .filter(route -> match(route, request)).findFirst()
                .orElseThrow(() -> new UnmatchedRouteException(
                        "No route found for request 'HTTP "
                                + request.getMethod().toUpperCase() + " "
                                + request.getPath() + "'.", request));
    }

    private boolean match(Route route, HightideRequest request) {
        String path = request.getMatchingPath();
        if (path.equals(route.getPath())) {
            return true;
        }
        if (nonNull(route.getMatchPattern())) {
            Matcher matcher = route.getMatchPattern().matcher(path);
            if (matcher.matches()) {
                //TODO Implement multiple reqParams extraction
                request.setPathParams(new Object[]{matcher.group(1)});
                return true;
            }
        }

        return false;
    }
}
