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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public final class Prototypes {

    private static final String DEFAULT_APP_TEMPLATE = "github:gzlabs/hightide-app-prototype";

    private Prototypes() {}

    public static void generateApp(String appName) throws IOException {
        generateApp(appName, DEFAULT_APP_TEMPLATE);
    }

    public static void generateApp(String appName, String appTemplateName) throws IOException {
        // Check if directory already exists
        String currentDir = System.getProperty("user.dir");
        Path appDir = Paths.get(currentDir, appName);
        if (Files.exists(appDir) && Files.isDirectory(appDir)) {
            System.out.println("A directory named '" + appName + "' already exists.");
            return;
        }

        Path tempDir = Files.createTempDirectory(Paths.get(currentDir), null);
        Path tempAppDir = TemplateFetcher.getPrototype(tempDir, appTemplateName);
        AppGenerator.generate(tempAppDir, appDir);
        FileUtils.removeRecursive(tempDir);
    }

}
