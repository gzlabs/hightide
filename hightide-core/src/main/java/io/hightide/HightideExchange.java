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

package io.hightide;

import io.hightide.route.Route;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class HightideExchange {

    private HightideRequest request;

    private Route resolvedRoute;

    private HightideResponse response;

    public HightideExchange request(HightideRequest request) {
        this.request = request;
        return this;
    }

    public HightideExchange response(HightideResponse response) {
        this.response = response;
        return this;
    }

    public HightideRequest getRequest() {
        return request;
    }

    public void setRequest(HightideRequest request) {
        this.request = request;
    }

    public Route getResolvedRoute() {
        return resolvedRoute;
    }

    public void setResolvedRoute(Route resolvedRoute) {
        this.resolvedRoute = resolvedRoute;
    }

    public HightideResponse getResponse() {
        return response;
    }

    public void setResponse(HightideResponse response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "HightideExchange{" +
                "request=" + request +
                ", resolvedRoute=" + resolvedRoute +
                ", response=" + response +
                '}';
    }
}
