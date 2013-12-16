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

import io.hightide.actions.InvocationAction;
import io.hightide.resources.ResourceLink;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class Route {

    private String path;

    private Pattern matchPattern;

    private InvocationAction invocationAction;

    private Set<ResourceLink> links;

    private String redirectTo;

    public Route path(String path) {
        this.setPath(path);
        return this;
    }

    public Route matchPattern(Pattern matchPattern) {
        this.setMatchPattern(matchPattern);
        return this;
    }

    public Route invocationAction(InvocationAction invocationAction) {
        this.setInvocationAction(invocationAction);
        return this;
    }

    public Route links(Set<ResourceLink> links) {
        setLinks(links);
        return this;
    }

    public Route redirectTo(String redirectTo) {
        setRedirectTo(redirectTo);
        return this;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Pattern getMatchPattern() {
        return matchPattern;
    }

    public void setMatchPattern(Pattern matchPattern) {
        this.matchPattern = matchPattern;
    }

    public InvocationAction getInvocationAction() {
        return invocationAction;
    }

    public void setInvocationAction(InvocationAction invocationAction) {
        this.invocationAction = invocationAction;
    }

    public Set<ResourceLink> getLinks() {
        return links;
    }

    public void setLinks(Set<ResourceLink> links) {
        this.links = links;
    }

    public String getRedirectTo() {
        return redirectTo;
    }

    public void setRedirectTo(String redirectTo) {
        this.redirectTo = redirectTo;
    }

    @Override
    public String toString() {
        return "Route{" +
                "path='" + path + '\'' +
                ", matchPattern=" + matchPattern +
                ", invocationAction=" + invocationAction +
                ", links=" + links +
                ", redirectTo='" + redirectTo + '\'' +
                '}';
    }
}
