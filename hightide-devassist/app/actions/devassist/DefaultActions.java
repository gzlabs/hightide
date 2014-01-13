package actions.devassist;

import io.hightide.ApplicationConfig;
import io.hightide.ApplicationContext;
import io.hightide.route.RoutesParser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class DefaultActions {

    private ApplicationConfig config = ApplicationContext.instance().getConfig();

    public Map<String, String> welcome() {
        String routesAsString = RoutesParser.getRoutesAsString();
        String appConfigAsString = config.getConfigAsString();
        Map<String, String> map = new HashMap<>();
        map.put("appConfigAsString", appConfigAsString);
        map.put("routesAsString", routesAsString);
        return map;
    }

}
