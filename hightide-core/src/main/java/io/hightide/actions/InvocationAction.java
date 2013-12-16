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

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class InvocationAction {

    private Class<?> clazz;

    private Method method;

    private Class<?>[] paramTypes;

    private String parsedAction;

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public String getParsedAction() {
        return parsedAction;
    }

    public void setParsedAction(String parsedAction) {
        this.parsedAction = parsedAction;
    }

    @Override
    public String toString() {
        return "InvocationAction{" +
                "clazz=" + clazz +
                ", method=" + method +
                ", paramTypes=" + Arrays.toString(paramTypes) +
                ", parsedAction='" + parsedAction + '\'' +
                '}';
    }
}
