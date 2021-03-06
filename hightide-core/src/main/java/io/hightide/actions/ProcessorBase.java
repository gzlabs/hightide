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

package io.hightide.actions;

import io.hightide.HightideExchange;

import java.util.Deque;
import java.util.Map;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public abstract class ProcessorBase {

    protected HightideExchange exchange;

    public HightideExchange getExchange() {
        return exchange;
    }

    public void setExchange(HightideExchange exchange) {
        this.exchange = exchange;
    }

    public Deque<String> param(String key) {
        return getExchange().getRequest().getReqParams().get(key);
    }

    public Map<String, Deque<String>> params() {
        return getExchange().getRequest().getReqParams();
    }

    public void responseCode(int httpCode) {
        getExchange().getResponse().setResponseCode(httpCode);
    }
}
