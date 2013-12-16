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

import io.hightide.HightideRequest;
import io.hightide.exceptions.ClientErrorException;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class UnmatchedRouteException extends ClientErrorException {

    private HightideRequest request;

    public UnmatchedRouteException(String message, HightideRequest request) {
        super(message);
        this.request = request;
    }

    public UnmatchedRouteException(String message) {
        this(message, null);
    }
}
