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

import io.hightide.security.SecurityMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public final class ApplicationContext {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationContext.class);

    private ApplicationConfig config;

    private ApplicationStage appStage;

    private SecurityMode securityMode;

    private ApplicationContext() {
        this.config = new ApplicationConfig();
    }

    private static class ApplicationContextHolder {
        public static final ApplicationContext INSTANCE = new ApplicationContext();
    }

    public static ApplicationContext instance() {
        return ApplicationContextHolder.INSTANCE;
    }

    public ApplicationConfig getConfig() {
        return config;
    }

    public ApplicationStage getAppStage() {
        if (isNull(appStage)) {
            appStage = ApplicationStage.ofString(getConfig().getString("app.stage"));
        }
        return appStage;
    }

    public SecurityMode getSecurityMode() {
        if (isNull(securityMode)) {
            securityMode = SecurityMode.ofString(getConfig().getString("app.security.mode"));
        }
        return securityMode;
    }

}
