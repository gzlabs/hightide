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

package io.hightide.exceptions;

import io.undertow.util.StatusCodes;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class ServerErrorException extends HttpErrorException {

    public ServerErrorException(String message) {
        super(message);
        setResponseCode(StatusCodes.INTERNAL_SERVER_ERROR);
    }

    public ServerErrorException(String message, Throwable t) {
        super(message, t);
        setResponseCode(StatusCodes.INTERNAL_SERVER_ERROR);
    }
}
