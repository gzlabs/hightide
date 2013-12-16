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

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public enum ApplicationStage {

    /**
     * Application in development stage.
     */
    DEV,

    /**
     * Application in test stage.
     */
    TEST,

    /**
     * Application in production stage.
     */
    PROD;

    public static ApplicationStage ofString(String str) {
        return ApplicationStage.valueOf(str.toUpperCase());
    }

    public String toString() {
        return this.name().toLowerCase();
    }

    public static final ApplicationStage DEFAULT_APP_STAGE = ApplicationStage.DEV;

}
