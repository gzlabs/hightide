package io.hightide.route;

import java.util.LinkedHashSet;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class RoutesManager {

    public LinkedHashSet<Route> loadRoutes() throws RouteParsingException {
        return RoutesParser.parse();
    }
}
