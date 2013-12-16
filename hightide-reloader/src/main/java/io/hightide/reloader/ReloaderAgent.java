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

package io.hightide.reloader;

import io.hightide.Application;
import io.hightide.ApplicationConfig;
import io.hightide.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class ReloaderAgent {

    public static final String CONFIG_DIR = "config";

    private static final Logger logger = LoggerFactory.getLogger(ReloaderAgent.class);

    private static Instrumentation inst;

    private static boolean enabled = false;

    private static ApplicationConfig config;

    private static String srcDir;

    private static RuntimeCompiler compiler;

    public static void premain(String agentArgs, Instrumentation inst) {
        logger.debug("Starting Reloader Agent...");
        ReloaderAgent.inst = inst;
        enabled = true;

        config = ApplicationContext.instance().getConfig();
        srcDir = config.getString("dirs.src");
        compiler = initCompiler();
        Application.setAppClassLoader(initClassloader());
        initSourceFileWatcher();
        initConfigFileWatcher();

    }

    public static void reload(ClassDefinition... defs) throws UnmodifiableClassException, ClassNotFoundException {
        logger.debug("Trying to hotswap class {}...", defs.getClass().getName());
        inst.redefineClasses(defs);
    }

    public static boolean isEnabled() {
        return enabled;
    }

    private static RuntimeCompiler initCompiler() {
        RuntimeCompiler compiler = new RuntimeCompiler(config.getString("dirs.gen-src"));
        logger.debug("Loading classes from " + srcDir + " directory.");
        try {
            final List<Path> sources = FileMatcher.match(srcDir, "glob:*.java");
            logger.trace("Found source files: {}", sources);
            if (sources.isEmpty()) {
                logger.info("No java files found; skipping dynamic compilation.");
            } else {
                compiler.run(sources);
                logger.info("Successfully compiled " + sources.size() + " java files.");
            }
        } catch (IOException e) {
            logger.error("Dynamic compilation failed on startup", e);
        }

        return compiler;
    }

    private static ReloadableClassLoader initClassloader() {
        try {
            logger.trace("Initializing classloader...");
            String generatedSrcDir = config.getString("dirs.gen-src");
            URL[] clUrls = new URL[]{new File(generatedSrcDir + File.separator).toURI().toURL()};
            return new ReloadableClassLoader(clUrls, generatedSrcDir);
        } catch (Exception e) {
            new RuntimeException("Failed to initialize ReloadableClassLoader");
        }
        return null;
    }

    private static void initSourceFileWatcher() {
        try {
            FileWatcher.spawn(Paths.get(srcDir), (ev, path) -> {
                switch (ev) {
                    case FILE_CREATED:
                    case FILE_MODIFIED:
                        try {
                            compiler.run(path);
                            if (ev.equals(FileWatcher.FileChangeEvent.FILE_CREATED)) {
                                Application.getAppClassLoader().loadClass(normalizeClassname(srcDir, path.toString()));
                            } else {
                                if (Application.getAppClassLoader() instanceof ReloadableClassLoader) {
                                    ((ReloadableClassLoader) Application.getAppClassLoader())
                                            .reloadClass(normalizeClassname(srcDir, path.toString()));
                                }
                            }
                        } catch (ClassNotFoundException e) {
                            logger.error("Failed to load class " + path, e);
                        } catch (IOException e) {
                            logger.error("Compilation failure for " + path, e);
                        } catch (UnsupportedOperationException e) {
                            logger.info("Hotswap failed to reload classes; restarting classloader.");
                            //TODO Bug - Reinitializing classloader leaves reference to view templates.
                            Application.setAppClassLoader(initClassloader());
                        }
                        break;
                    case FILE_DELETED:
                        //TODO - Delete class file
                        Application.setAppClassLoader(initClassloader());
                }

            });
        } catch (Exception e) {
            System.err.println("Failed to start file watcher");
            e.printStackTrace();
        }
    }

    private static void initConfigFileWatcher() {
        if (!new File(CONFIG_DIR).exists()) {
            logger.warn("Failed to start file watcher for directory {}; directory doesn't exist!", CONFIG_DIR);
            return;
        }
        try {
            FileWatcher.spawn(Paths.get(CONFIG_DIR), (ev, path) -> {
                switch (ev) {
                    case FILE_CREATED:
                    case FILE_MODIFIED:
                        logger.debug("Config file {} modified.", path.toString());
                        //RouteHandler.instance().loadRoutes();
                        break;
                    case FILE_DELETED:
                        //TODO - If a config is deleted ???
                }

            });
        } catch (Exception e) {
            logger.warn("Failed to start file watcher for directory " + CONFIG_DIR + ": ", e);
        }
    }

    private static String normalizeClassname(String prefix, String name) {
        String str = name.substring((prefix + File.separator).length()).replace(File.separator, ".").replace(".java", "");
        return name.replace(prefix + File.separator, "").replace(File.separator, ".").replace(".java", "");
    }


}
