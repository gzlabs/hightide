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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.hightide.HightideExchange;
import io.hightide.exceptions.ServerErrorException;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class JacksonJsonRenderer implements RenderEngine {

    private ObjectMapper mapper;

    public JacksonJsonRenderer() {
        mapper = new ObjectMapper();
        SimpleModule defaultModule = new SimpleModule("DefaultHightideModule", new Version(1, 0, 0, null));
//        defaultModule.addSerializer(Record.class, new JooqRecordJsonSerializer());
        mapper.registerModule(defaultModule);
    }

    @Override
    public String render(HightideExchange exchange) throws ServerErrorException {
        try {
            return mapper.writeValueAsString(exchange.getResponse().getReturnedObj());
        } catch (JsonProcessingException e) {
            throw new ServerErrorException("Failed to automatically convert object to json", e);
        }

    }

}
