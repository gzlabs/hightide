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

package io.hightide.renderers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public final class Renderer {

    private static final Map<String, RenderEngine> engines = new HashMap<>();

    private Renderer() {}

    public static Optional<RenderEngine> of(String mediaType) {
        switch(mediaType) {
            case "text/html":
            case "application/xhtml+xml":
                return checkInitAndReturn("text/html", RythmHtmlRenderer.class);
            case "application/xml":
                return checkInitAndReturn(mediaType, JaxbXmlRenderer.class);
            case "text/plain":
                return checkInitAndReturn(mediaType, DefaultTextRenderer.class);
            case "application/json":
                return checkInitAndReturn(mediaType, JacksonJsonRenderer.class);
            default:
                return Optional.empty();
        }
    }

    private static Optional<RenderEngine> checkInitAndReturn(String mediaType, Class<?> renderEngineClass) {
        RenderEngine renderEngine = engines.get(mediaType);
        if (isNull(renderEngine)) {
            try {
                renderEngine = (RenderEngine) renderEngineClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                return Optional.empty();
            }
            engines.put(mediaType, renderEngine);
        }
        return Optional.of(renderEngine);
    }
}
