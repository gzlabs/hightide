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

import io.hightide.AcceptHeaderElement;
import io.hightide.HightideExchange;
import io.hightide.HightideResponse;
import io.hightide.renderers.RenderEngine;
import io.hightide.renderers.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static io.undertow.util.StatusCodes.UNSUPPORTED_MEDIA_TYPE;
import static java.util.Objects.nonNull;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class RepresentationHandler extends HightideHandler {

    private static final Logger logger = LoggerFactory.getLogger(RepresentationHandler.class);

    @Override
    public void handleRequest(HightideExchange exchange) throws Exception {
        logger.debug("Sending response...");
        HightideResponse resp = exchange.getResponse();

        for (AcceptHeaderElement acceptedType : exchange.getRequest().getAcceptedTypes()) {
            Optional<RenderEngine> renderEngine = Renderer.of(acceptedType.getMediaType());
            if (renderEngine.isPresent()) {
                String output = renderEngine.get().render(exchange);
                resp.contentType(acceptedType.getMediaType()).content(output);
            }
            if (nonNull(resp.getContentType())) {
                exchange.setResponse(resp);
                return;
            }
        }

        resp.setResponseCode(UNSUPPORTED_MEDIA_TYPE);
    }
}
