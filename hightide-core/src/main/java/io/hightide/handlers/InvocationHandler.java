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
import io.hightide.actions.InvocationAction;
import io.hightide.actions.ProcessorBase;
import io.hightide.exceptions.ServerErrorException;
import io.hightide.resources.IResource;
import io.hightide.resources.ResourceLink;
import io.hightide.route.Route;
import io.undertow.server.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static io.undertow.util.StatusCodes.NO_CONTENT;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public final class InvocationHandler extends HightideHandler {

    private static final Logger logger = LoggerFactory.getLogger(InvocationHandler.class);

    public InvocationHandler(HttpHandler next) {
        super(next);
    }

    @Override
    public void handleRequest(HightideExchange exchange) throws Exception {
        logger.debug("Invocation started...");
        Object returnedObj = invoke(exchange, exchange.getResolvedRoute());
        exchange.getResponse().setReturnedObj(returnedObj);
    }

    private Object invoke(HightideExchange exchange, Route route) throws Exception {
        HightideRequest req = exchange.getRequest();

        InvocationAction icd = route.getInvocationAction();
        Object obj = icd.getClazz().newInstance();
        ProcessorBase processor = null;
        if (obj instanceof ProcessorBase) {
            try {
                processor = (ProcessorBase) obj;
            } catch (ClassCastException e) {
                throw new ServerErrorException("Action class should extend io.hightide.mvc.ProcessorSupport", e);
            }
            processor.setExchange(exchange);
        }

        Method invokeMethod = icd.getMethod();

        List<Object> args = new ArrayList<>();
        if (nonNull(req.getPathParams())) {
            for (int i = 0; i < req.getPathParams().length; i++) {
                Constructor<?> declaredConstructor =
                        icd.getMethod().getParameterTypes()[i].getDeclaredConstructor(req.getPathParams()[i].getClass());
                args.add(declaredConstructor.newInstance(req.getPathParams()[i]));
            }
        }

        try {
            Object ret;
            if (args.isEmpty()) {
                ret = invokeMethod.invoke(isNull(processor) ? obj : processor, null);
            } else if (args.size() == 1) {
                ret = invokeMethod.invoke(isNull(processor) ? obj : processor, args.get(0));
            } else {
                ret = invokeMethod.invoke(isNull(processor) ? obj : processor, args);
            }

            if (nonNull(ret) && ret instanceof IResource) {
                IResource res = (IResource) ret;
                //TODO Refactor Me!
                if (isNull(exchange.getResolvedRoute().getLinks())) {
                    res.setLinks(null);
                } else {
                    res.setLinks(exchange.getResolvedRoute().getLinks().stream().map(l -> {
                        ResourceLink rl = new ResourceLink();
                        rl.setHref(l.getHref().replaceFirst("@(\\w+)", String.valueOf(args.get(0))));
                        rl.setMethod(l.getMethod());
                        rl.setRel(l.getRel());
                        rl.setTitle(l.getTitle());
                        rl.setType(l.getType());
                        return rl;
                    }).collect(toSet()));
                }
                return res;
            }
            if (isNull(ret)) {
                exchange.getResponse().setResponseCode(NO_CONTENT);
            }
            return ret;
        } catch (Exception e) {
            /** Removed InvocationTargetException from stacktrace, rethrow the actual cause. */
            String msg = String.format("Failed to invoke action %s with arguments %s", icd.getMethod(), args);
            throw new Exception(msg, e.getCause());
        }
    }
}
