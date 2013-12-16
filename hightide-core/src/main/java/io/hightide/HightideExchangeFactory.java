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

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.util.AttachmentKey;
import io.undertow.util.Headers;
import io.undertow.util.QValueParser;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toCollection;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public final class HightideExchangeFactory {

    public static final AttachmentKey<HightideExchange> HIGHTIDE_EXCHANGE = AttachmentKey.create(HightideExchange.class);

    private HightideExchangeFactory() {}

    public static HightideExchange get(final HttpServerExchange exchange) {
        HightideExchange existing;
        if (nonNull(existing = exchange.getAttachment(HIGHTIDE_EXCHANGE))) {
            return existing;
        }
        HightideExchange he = newHighTideExchange(exchange);
        exchange.putAttachment(HIGHTIDE_EXCHANGE, he);
        return he;
    }

    private static HightideExchange newHighTideExchange(final HttpServerExchange exchange) {
        List<AcceptHeaderElement> acceptHeaderElements =
                parseAcceptHeader(exchange.getRequestHeaders().get(Headers.ACCEPT));
        Map<String,Deque<String>> queryParams = exchange.getQueryParameters();

        String method = exchange.getRequestMethod().toString();
        String path = exchange.getRequestPath();
        HightideRequest req = new HightideRequest()
                .method(method)
                .path(path)
                .reqParams(queryParams)
                .acceptedTypes(acceptHeaderElements);

        FormData formData = exchange.getAttachment(FormDataParser.FORM_DATA);

        if (nonNull(formData)) {
            Map<String, Deque<String>> params = new HashMap<>();
            //TODO Check request parameters conversion; will Deque actually contain more than one value?
            formData.forEach((key) -> params.put(key,
                    formData.get(key).stream()
                            .filter(formValue -> !formValue.isFile())
                            .map(FormData.FormValue::getValue).collect(toCollection(ArrayDeque::new))));
            params.putAll(req.getReqParams());
            req.setReqParams(params);
        }

        HightideResponse resp = new HightideResponse();

        return new HightideExchange().request(req).response(resp);
    }

    private static List<AcceptHeaderElement> parseAcceptHeader(List<String> acceptHeader) {
        final List<List<QValueParser.QValueResult>> mediaTypes = QValueParser.parse(acceptHeader);
        List<AcceptHeaderElement> list = new ArrayList<>();
        for (List<QValueParser.QValueResult> mediaType : mediaTypes) {
            list.addAll(mediaType.stream().map(
                    result -> new AcceptHeaderElement().mediaType(result.getValue()).q(result.getQvalue())
            ).collect(Collectors.<AcceptHeaderElement>toList()));
        }
        return list;
    }

    private static Object[] parseParameters() {
        return null;
    }
}
