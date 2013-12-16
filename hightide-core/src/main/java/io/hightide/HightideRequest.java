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

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class HightideRequest {

    private String method;

    private String path;

    private String matchingPath;

    private Object[] pathParams;

    private Map<String, Deque<String>> reqParams;

    private List<AcceptHeaderElement> acceptedTypes;

    public HightideRequest method(String method) {
        setMethod(method);
        return this;
    }

    public HightideRequest path(String path) {
        setPath(path);
        return this;
    }

    public HightideRequest matchingPath(String matchingPath) {
        setMatchingPath(matchingPath);
        return this;
    }

    public HightideRequest pathParams(Object[] pathParams) {
        setPathParams(pathParams);
        return this;
    }

    public HightideRequest reqParams(Map<String, Deque<String>> reqParams) {
        setReqParams(reqParams);
        return this;
    }

    public HightideRequest acceptedTypes(List<AcceptHeaderElement> acceptedTypes) {
        setAcceptedTypes(acceptedTypes);
        return this;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Object[] getPathParams() {
        return pathParams;
    }

    public void setPathParams(Object[] pathParams) {
        this.pathParams = pathParams;
    }

    public String getMatchingPath() {
        return matchingPath;
    }

    public void setMatchingPath(String matchingPath) {
        this.matchingPath = matchingPath;
    }

    public Map<String, Deque<String>> getReqParams() {
        return reqParams;
    }

    public void setReqParams(Map<String, Deque<String>> reqParams) {
        this.reqParams = reqParams;
    }

    public List<AcceptHeaderElement> getAcceptedTypes() {
        return acceptedTypes;
    }

    public void setAcceptedTypes(List<AcceptHeaderElement> acceptedTypes) {
        this.acceptedTypes = acceptedTypes;
    }

    @Override
    public String toString() {
        return "HightideRequest{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", pathParams=" + pathParams +
                ", reqParams=" + reqParams +
                ", acceptedTypes=" + acceptedTypes +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path, pathParams, acceptedTypes);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (obj instanceof HightideRequest) {
            HightideRequest other = (HightideRequest) obj;

            return Objects.equals(method, other.method) && Objects.equals(path, other.path);
        }

        return false;
    }

}
