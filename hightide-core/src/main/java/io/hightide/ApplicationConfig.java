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

import com.typesafe.config.Config;

import java.io.File;
import java.util.List;
import java.util.Properties;

import static com.typesafe.config.ConfigFactory.load;
import static com.typesafe.config.ConfigFactory.parseFile;

public final class ApplicationConfig {

    public static final String CONFIG_DIR = "config";

    private final Config config;

    public ApplicationConfig() {
        this.config = parseFile(new File(CONFIG_DIR + File.separator + "application.conf"))
                .withFallback(load()).resolve();
    }

    protected ApplicationConfig(Config config) {
        this.config = config;
    }

    public Object get(String key) {
        return config.getAnyRef(key);
    }

    public String getString(String key) {
        return config.getString(key);
    }

    public List<String> getStrings(String key) {
        return config.getStringList(key);
    }

    public Integer getInt(String key) {
        return config.getInt(key);
    }

    public Boolean getBoolean(String key) {
        return config.getBoolean(key);
    }

    public Integer count() {
        return config.entrySet().size();
    }

    public Properties getProperties() {
        Properties props = new Properties();
        config.entrySet().forEach(e -> props.put(e.getKey(), e.getValue().unwrapped()));
        return props;
    }

    public ApplicationConfig getConfig(String key) {
        return new ApplicationConfig(config.getConfig(key));
    }

}
