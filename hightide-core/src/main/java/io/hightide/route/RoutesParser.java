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

package io.hightide.route;

import com.typesafe.config.*;
import io.hightide.Application;
import io.hightide.ApplicationConfig;
import io.hightide.ApplicationContext;
import io.hightide.actions.InvocationAction;
import io.hightide.resources.ResourceLink;
import io.hightide.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public final class RoutesParser {

    private static final Logger logger = LoggerFactory.getLogger(RoutesParser.class);

    private static final Pattern urlParamPattern = Pattern.compile("\\@(\\w+)");

    private static final Pattern methodArgsPattern = Pattern.compile("\\((\\w+)\\s(\\w+)\\)");

    private static final ApplicationConfig config = ApplicationContext.instance().getConfig();

    private static String routesStr;

    private RoutesParser() {
    }

    public static LinkedHashSet<Route> parse() throws RouteParsingException {
        return parse("routes.conf");
    }

    private static LinkedHashSet<Route> parse(String routesFileName) throws RouteParsingException {
        LinkedHashSet<Route> routes = new LinkedHashSet<>();
        ConfigObject confRoutes;
        try {
            routesStr = FileUtils.readFile(config.getString("dirs.routes") + File.separator + routesFileName, StandardCharsets.UTF_8);

            confRoutes = ConfigFactory.parseFile(
                    new File(config.getString("dirs.routes") + File.separator + routesFileName),
                    ConfigParseOptions.defaults())
                    .withFallback(ConfigFactory.load("default-routes.conf"))
                    .getConfig("routes")
                    .resolve().root();
        } catch (ConfigException.Missing | IOException e) {
            throw new RouteParsingException("Failed to parse routes; Make sure routes.conf file is in the classpath!");
        }

        for (Map.Entry<String, ConfigValue> entry : confRoutes.entrySet()) {
            String keyToken = entry.getKey();
            switch (keyToken) {
                case "resources": // Handle Route Resources
                    logger.warn("RESOURCES BASED ROUTING NOT IMPLEMENTED YET!!! Resources {} will not be parsed.", entry.getValue());
                    break;
                case "static":
                    logger.warn("STATIC FILE ROUTING NOT IMPLEMENTED YET!!! Static routes {} will not be parsed.", entry.getValue());
                    break;
                default: // Handle Route Paths
                    String mainPath = keyToken;
                    String path;
                    String action = null;
                    Set<ResourceLink> links = null;
                    ConfigValue val = entry.getValue();
                    switch (val.valueType()) {
                        case OBJECT: // Path value contains an object
                            ConfigObject obj = (ConfigObject) val;

                            for (Map.Entry<String, ConfigValue> _entry : obj.entrySet()) {
                                String _keyToken = _entry.getKey();
                                String redirect = null;
                                switch (_keyToken) {
                                    case "get":
                                    case "post":
                                    case "put":
                                    case "delete": //After path you find an HTTP method (handle them all the same way, for now)
                                        path = mainPath + "." + _keyToken; // Add HTTP method to path
                                        ConfigValue _val = _entry.getValue();
                                        switch (_val.valueType()) {
                                            case OBJECT:
                                                ConfigObject _obj = (ConfigObject) _val;

                                                for (Map.Entry<String, ConfigValue> __entry : _obj.entrySet()) {
                                                    String __keyToken = __entry.getKey();
                                                    switch (__keyToken) {
                                                        case "action":
                                                            action = (String) __entry.getValue().unwrapped();
                                                            break;
                                                        case "redirect":
                                                            redirect = (String) __entry.getValue().unwrapped();
                                                            break;
                                                        case "links":
                                                            ConfigList lst = (ConfigList) __entry.getValue();

                                                            links = lst.stream().map((v) -> {
                                                                ConfigObject o = (ConfigObject) v;
                                                                ResourceLink rl = new ResourceLink();
                                                                o.entrySet().stream().forEach(e -> {
                                                                    switch (e.getKey()) {
                                                                        case "href":
                                                                            //TODO href needs normalization Parameter parsing and http method removal.
                                                                            String href = (String) e.getValue().unwrapped();
                                                                            int idx = href.indexOf(".");
                                                                            if (idx > 0) {
                                                                                rl.setMethod(href.substring(idx + 1));
                                                                                rl.setHref(href.substring(0, idx));
                                                                            }
                                                                            break;
                                                                        case "rel":
                                                                            rl.setRel((String) e.getValue().unwrapped());
                                                                            break;
                                                                        case "title":
                                                                            rl.setTitle((String) e.getValue().unwrapped());
                                                                            break;
                                                                        default:
                                                                            logger.warn("Unknown attribute '{}' on links object.", e.getKey());
                                                                    }
                                                                });
                                                                return rl;
                                                            }).collect(toSet());
                                                            break;
                                                        default:
                                                            logger.warn("NOT IMPLEMENTED YET! Token {} want be parsed.", __keyToken);
                                                    }

                                                }
                                                break;
                                            case STRING: // If value is a string it should be an action
                                                action = (String) _val.unwrapped();
                                                break;
                                            default:
                                                throw new RouteParsingException("Failed to parse route; shouldn't come in here!");
                                        }
                                        break;
                                    default:
                                        throw new RouteParsingException("Failed to parse route; shouldn't come in here!");
                                }
                                action = config.getString("app.packages.actions") + "." + action;
                                String paramReplacePattern = null;
                                Matcher methodArgsMatcher = methodArgsPattern.matcher(action);
                                if (methodArgsMatcher.find()) {
                                    action = action.substring(0, methodArgsMatcher.start());
                                    String paramType = methodArgsMatcher.group(1);
                                    Class<?> paramClass;
                                    if (!paramType.contains(".")) {
                                        paramType = "java.lang." + paramType;
                                    }
                                    String paramName = methodArgsMatcher.group(2);
                                    try {
                                        paramClass = Class.forName(paramType);
                                        if (Number.class.isAssignableFrom(paramClass)) {
                                            paramReplacePattern = "(\\\\d+)";
                                        } else {
                                            paramReplacePattern = "(\\\\w+)";
                                        }
                                    } catch (ClassNotFoundException e) {
                                        logger.error("Could not find class for parameter type " + paramType);
                                    }
                                }

                                Pattern pattern = null;
                                Matcher urlParamMatcher = urlParamPattern.matcher(path);
                                if (urlParamMatcher.find()) {
                                    pattern = Pattern.compile(urlParamMatcher.replaceAll(paramReplacePattern));
                                }

                                try {
                                    Route r = new Route()
                                            .path(path)
                                            .matchPattern(pattern)
                                            .invocationAction(extractInvocationAction(action))
                                            .links(links)
                                            .redirectTo(redirect);
                                    logger.debug("Parsed route: " + r);
                                    routes.add(r);
                                } catch (ClassNotFoundException | NoSuchMethodException e) {
                                    throw new RouteParsingException("Failed to parse route; action class or method does not exist.", e);
                                }
                            }
                            break;
                        case STRING: // If Path value is a string it should be an action
                            break;
                        default:
                            throw new RouteParsingException("Failed to parse route; shouldn't come in here!");
                    }

            }

        }

        return routes;
    }

    private static InvocationAction extractInvocationAction(String action) throws ClassNotFoundException, NoSuchMethodException {
        String className = action.substring(0, action.lastIndexOf("."));
        String methodName = action.substring(action.lastIndexOf(".") + 1);

//        Object[] reqParams = new Object[5];
//        if (nonNull(paramsString)) {
//            reqParams = paramsString.split(",");
//        }
//        reqParams[0] = Long.parseLong(reqParams[0].toString());

        InvocationAction ia = new InvocationAction();
        ia.setParsedAction(action);
        Class<?> clazz = findClass(className);
        ia.setClazz(clazz);
        Method method = findMethod(clazz, methodName);
        ia.setMethod(method);
        ia.setParamTypes(method.getParameterTypes());
        return ia;
    }

    private static Class<?> findClass(String className) throws ClassNotFoundException {
        return Application.getAppClassLoader().loadClass(className);
    }

    private static Method findMethod(Class<?> clazz, String methodName) throws NoSuchMethodException {
        Method[] methods = clazz.getDeclaredMethods();
        List<Method> filtered = stream(methods)
                .filter(method -> method.getName().equals(methodName))
                .collect(toList());

        if (filtered.size() == 1) {
            return filtered.get(0);
        } else if (filtered.isEmpty()) {
            throw new NoSuchMethodException("Method " + methodName + " does not exist in class " + clazz.getName());
        } else {
            throw new UnsupportedOperationException(
                    "Could not resolve method " + methodName + " for class " + clazz.getName()
                            + "; action resolution based on parameters definition is not supported yet.");
        }

    }

    public static String getRoutesAsString() {
        return routesStr;
    }
}
