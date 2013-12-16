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
import io.hightide.HightideExchangeFactory;
import io.hightide.HightideResponse;
import io.hightide.exceptions.HttpErrorException;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.util.AttachmentKey;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import static java.util.Objects.nonNull;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public abstract class HightideHandler implements HttpHandler {

    public static final AttachmentKey<Exception> EXCEPTION_ATTACHMENT = AttachmentKey.create(Exception.class);

    private volatile HttpHandler notFoundHandler = ResponseCodeHandler.HANDLE_404;

    private HttpHandler next;

    protected HightideHandler() {}

    protected HightideHandler(HttpHandler next) {
        this.next = next;
    }

    public abstract void handleRequest(final HightideExchange exchange) throws Exception;

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        HightideExchange hightideExchange = HightideExchangeFactory.get(exchange);
        try {
            handleRequest(hightideExchange);
        } catch (Exception e) {

            if (e instanceof HttpErrorException) {
                HttpErrorException error = (HttpErrorException) e;
                exchange.setResponseCode(error.getResponseCode());
            } else {
                exchange.setResponseCode(StatusCodes.INTERNAL_SERVER_ERROR);
            }
            exchange.putAttachment(HightideHandler.EXCEPTION_ATTACHMENT, e);
            exchange.endExchange();
            throw e;
        }

        if (nonNull(hightideExchange.getResponse().getContent())) {
            HightideResponse response = hightideExchange.getResponse();
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, response.getContentType());
            exchange.getResponseSender().send(response.getContent());
        }
        if (!exchange.isComplete()) {
            if (nonNull(next)) {
                next.handleRequest(exchange);
            } else {
                notFoundHandler.handleRequest(exchange);
            }
        }
    }

    protected void setNext(HightideHandler next) {
        this.next = next;
    }

}
