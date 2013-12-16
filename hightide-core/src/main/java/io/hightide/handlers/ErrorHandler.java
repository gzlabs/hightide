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

import io.hightide.ApplicationContext;
import io.hightide.ApplicationStage;
import io.undertow.io.Sender;
import io.undertow.server.DefaultResponseListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.util.Objects.nonNull;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class ErrorHandler implements HttpHandler {

    private volatile HttpHandler next = ResponseCodeHandler.HANDLE_404;

    private boolean devMode = false;

    public ErrorHandler(HttpHandler next) {
        devMode = ApplicationStage.DEV.equals(ApplicationContext.instance().getAppStage());
        this.next = next;
    }

    public ErrorHandler(boolean devMode, HttpHandler next) {
        this(next);
        this.devMode = devMode;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.addDefaultResponseListener(new DefaultResponseListener() {
            @Override
            public boolean handleDefaultResponse(final HttpServerExchange exchange) {
                if (!exchange.isResponseChannelAvailable()) {
                    return false;
                }

                if (exchange.getResponseCode() >= 400) {
                    String exceptionString = printException(exchange.getAttachment(HightideHandler.EXCEPTION_ATTACHMENT));
                    String message = exchange.getResponseCode() == 404 ?
                            "Wrong address, no one lives here!" : "Server's on fire, run for your life!";
                    final String errorPage = "<html><head><title>Error</title></head><body>"
                            + "<h1>"+ message + "</h1>"
                            + exchange.getResponseCode() + " - " + StatusCodes.getReason(exchange.getResponseCode())
                            + exceptionString
                            + "</body></html>";
                    exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, "" + errorPage.length());
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                    Sender sender = exchange.getResponseSender();
                    sender.send(errorPage);
                    return true;
                }
                return false;
            }
        });
        next.handleRequest(exchange);
    }

    private String printException(Throwable t) {
        StackTraceElement ste = t.getStackTrace().length > 0 ? t.getStackTrace()[0] : null;
        String message;
        if (nonNull(t.getMessage())) {
            message = htmlify(t.getMessage());
        } else {
            message = "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<div><strong>Error Message:</strong> ").append(t.getClass().getName()).append(" - ").append(message).append("<br/><br/>");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        t.printStackTrace(new PrintStream(baos));
        sb.append("<div><Strong>Stacktrace</strong><br/>").append(htmlify(baos.toString())).append("</div>");
        //sb.append("File name: ").append(ste.getFileName()).append(" at line ").append(ste.getLineNumber()).append(nl);
        //sb.append("Method Called: ").append(ste.getClassName()).append(".").append(ste.getMethodName());
        return sb.toString();
    }

    private String htmlify(String str) {
        return StringEscapeUtils.escapeHtml4(str).replaceAll("\\n","<br/>").replaceAll("\\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
    }
}
